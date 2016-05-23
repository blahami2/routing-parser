/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.osm.pbf;

import cz.certicon.routing.data.DataSource;
import cz.certicon.routing.model.basic.TimeUnits;
import cz.certicon.routing.parser.data.osm.OsmDataSource;
import cz.certicon.routing.parser.data.osm.OsmDataTarget;
import cz.certicon.routing.parser.data.osm.OsmDataTargetFactory;
import cz.certicon.routing.parser.model.entity.osm.*;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openstreetmap.osmosis.osmbinary.BinaryParser;
import org.openstreetmap.osmosis.osmbinary.Osmformat;
import org.openstreetmap.osmosis.osmbinary.file.BlockInputStream;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class OsmPbfDataSource implements OsmDataSource {

    private static final double SPEED_EPS = 10E-2;

    private static final int THREAD_COUNT = 1;

    private final DataSource dataSource;

    public OsmPbfDataSource( DataSource dataSource ) throws IOException {
        this.dataSource = dataSource;
    }

    @Override
    public boolean read( OsmDataTargetFactory targetFactory ) throws IOException {
        OsmBinaryParser brad = new OsmBinaryParser( targetFactory, THREAD_COUNT );
        BlockInputStream blockInputStream = new BlockInputStream( new BufferedInputStream( dataSource.getInputStream() ), brad );
        blockInputStream.process();
        return false;
    }

    @Override
    public boolean read( OsmDataTarget target ) throws IOException {
//        OsmBinaryParser brad = new OsmBinaryParser( this );
//        BlockInputStream blockInputStream = new BlockInputStream( new BufferedInputStream( dataSource.getInputStream() ), brad );
//        blockInputStream.process();
        return false;
    }

    private class OsmBinaryParser extends BinaryParser {

        private final TimeMeasurement time;
        private final BlockingQueue<OsmDataTarget> queue;
        private final ExecutorService threadPool;
        private final OsmDataTargetFactory factory;
        private final int version = 1;
        private final int changeset = 1;
        private final boolean visible = true;

        public OsmBinaryParser( OsmDataTargetFactory factory, int threadCount ) {
            this.factory = factory;
            this.threadPool = Executors.newFixedThreadPool( threadCount, new TargetThreadFactory( factory ) );
            this.queue = new ArrayBlockingQueue<>( threadCount );
            for ( int i = 0; i < threadCount; i++ ) {
                queue.add( factory.createOsmDataTarget() );
            }
            this.time = new TimeMeasurement();
            this.time.setTimeUnits( TimeUnits.MILLISECONDS );
            this.time.start();
        }

        @Override
        protected void parseRelations( List<Osmformat.Relation> list ) {
            if ( !list.isEmpty() ) {
                threadPool.submit( new RelationsWriterTask( queue, this, list ) );
            }
        }

        @Override
        protected void parseDense( Osmformat.DenseNodes nodes ) {
            if ( nodes.getIdCount() > 0 ) {
                threadPool.submit( new DenseNodesWriterTask( queue, this, nodes ) );
            }
        }

        @Override
        protected void parseNodes( List<Osmformat.Node> nodes ) {
            if ( !nodes.isEmpty() ) {
                threadPool.submit( new NodesWriterTask( queue, this, nodes ) );
            }
        }

        @Override
        protected void parseWays( List<Osmformat.Way> ways ) {
            if ( !ways.isEmpty() ) {
                threadPool.submit( new WaysWriterTask( queue, this, ways ) );
            }
        }

        @Override
        protected void parse( Osmformat.HeaderBlock hb ) {
        }

        @Override
        public void complete() {
            System.out.println( "Complete reading in time " + time.stop() + " ms! Awaiting termination..." );
            try {
                threadPool.shutdown();
                threadPool.awaitTermination( Integer.MAX_VALUE, TimeUnit.DAYS );
            } catch ( InterruptedException ex ) {
                Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
            }
            System.out.println( "Terminated." );
        }

        public String getStringByIdPublic( int id ) {
            return getStringById( id );
        }

        public Date getDatePublic( Osmformat.Info info ) {
            return getDate( info );
        }

        public int getVersion() {
            return version;
        }

        public int getChangeset() {
            return changeset;
        }

        public boolean getVisible() {
            return visible;
        }

        public int getDateGranularity() {
            return date_granularity;
        }

    }

    private static abstract class WriterTask implements Runnable {

        private final BlockingQueue<OsmDataTarget> queue;
        private final OsmBinaryParser parser;

        public WriterTask( BlockingQueue<OsmDataTarget> queue, OsmBinaryParser parser ) {
            this.queue = queue;
            this.parser = parser;
            System.out.println( "created: " + getClass().getSimpleName() );
        }

        @Override
        public void run() {
            try {
                System.out.println( "polling: " + getClass().getSimpleName() );
//                OsmDataTarget target = queue.take();
                OsmDataTarget target = ( (TargetThread) Thread.currentThread() ).getTarget();
                System.out.println( "polled: " + target );
                run( target );
                queue.put( target );
                System.out.println( "returned: " + target );
            } catch ( InterruptedException ex ) {
                Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }

        protected OsmBinaryParser getParser() {
            return parser;
        }

        protected String getStringById( int id ) {
            return parser.getStringByIdPublic( id );
        }

        protected Timestamp getTimestamp( Osmformat.Info info ) {
            if ( info == null ) {
                return new Timestamp( 0 );
            }
            return new Timestamp( parser.getDatePublic( info ).getTime() );
        }

        public int getVersion() {
            return parser.getVersion();
        }

        public int getChangeset() {
            return parser.getChangeset();
        }

        public double parseLat( long degree ) {
            return parser.parseLat( degree );
        }

        public double parseLon( long degree ) {
            return parser.parseLon( degree );
        }

        public boolean getVisible() {
            return parser.getVisible();
        }

        public int getDateGranularity() {
            return parser.getDateGranularity();
        }

        protected abstract void run( OsmDataTarget osmDataTarget );
    }

    private static class RelationsWriterTask extends WriterTask {

        private final List<Osmformat.Relation> relations;

        public RelationsWriterTask( BlockingQueue<OsmDataTarget> queue, OsmBinaryParser parser, List<Osmformat.Relation> relations ) {
            super( queue, parser );
            this.relations = relations;
        }

        @Override
        protected void run( OsmDataTarget osmDataTarget ) {
            relations.stream().map( ( relation ) -> {
                long id = relation.getId();
//                System.out.println( "relation: " + id );
                List<OsmTag> tags = new ArrayList<>();
                for ( int i = 0; i < relation.getKeysCount(); i++ ) {
                    tags.add( new OsmTag( id, getStringById( relation.getKeys( i ) ), getStringById( relation.getVals( i ) ), getVersion() ) );
                }
                long lastMid = 0;
                List<OsmRelationMember> members = new ArrayList<>();
                for ( int i = 0; i < relation.getMemidsCount(); i++ ) {
                    long mid = lastMid + relation.getMemids( i );
                    lastMid = mid;
                    String role = getStringById( relation.getRolesSid( i ) );

                    OsmRelationMember.Type type = null;
                    if ( null != relation.getTypes( i ) ) {
                        switch ( relation.getTypes( i ) ) {
                            case NODE:
                                type = OsmRelationMember.Type.NODE;
                                break;
                            case WAY:
                                type = OsmRelationMember.Type.WAY;
                                break;
                            case RELATION:
                                type = OsmRelationMember.Type.RELATION;
                                break;
                            default:
//                                throw new AssertionError("Unknown relation type");
                        }
                    }

                    members.add( new OsmRelationMember( mid, type, role, i ) );
                }
                OsmRelation tmp;
                if ( relation.hasInfo() ) {
                    Osmformat.Info info = relation.getInfo();
                    tmp = new OsmRelation( id, getTimestamp( info ), info.getVersion(), info.getVisible(), info.getChangeset(), info.getUserSid(), tags, members );
                } else {
                    tmp = new OsmRelation( id, getTimestamp( null ), getVersion(), getVisible(), getChangeset(), 0, tags, members );
                }
                return tmp;
            } ).forEach( ( tmp ) -> {
                try {
//                    System.out.println( "inserting" );
                    osmDataTarget.insertRelation( tmp );
                } catch ( IOException ex ) {
                    Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
                }
            } );
        }
    }

    private static class WaysWriterTask extends WriterTask {

        private final List<Osmformat.Way> ways;

        public WaysWriterTask( BlockingQueue<OsmDataTarget> queue, OsmBinaryParser parser, List<Osmformat.Way> ways ) {
            super( queue, parser );
            this.ways = ways;
        }

        @Override
        protected void run( OsmDataTarget osmDataTarget ) {
            ways.stream().map( ( way ) -> {
                long id = way.getId();
                List<OsmTag> tags = new ArrayList<>();
                for ( int i = 0; i < way.getKeysCount(); i++ ) {
                    tags.add( new OsmTag( id, getStringById( way.getKeys( i ) ), getStringById( way.getVals( i ) ), getVersion() ) );
                }
                long lastRef = 0;
                List<OsmWayNode> nodes = new ArrayList<>();
                for ( int i = 0; i < way.getRefsCount(); i++ ) {
                    lastRef += way.getRefs( i );
                    nodes.add( new OsmWayNode( id, lastRef, i ) );
                }
                OsmWay tmp;
                if ( way.hasInfo() ) {
                    Osmformat.Info info = way.getInfo();
                    tmp = new OsmWay( id, getTimestamp( info ), info.getVersion(), info.getVisible(), info.getChangeset(), info.getUserSid(), tags, nodes );
                } else {
                    tmp = new OsmWay( id, getTimestamp( null ), getVersion(), getVisible(), getChangeset(), 0, tags, nodes );
                }
                return tmp;
            } ).forEach( ( tmp ) -> {
                try {
                    osmDataTarget.insertWay( tmp );
                } catch ( IOException ex ) {
                    Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
                }
            } );
        }
    }

    private static class NodesWriterTask extends WriterTask {

        private final List<Osmformat.Node> nodes;

        public NodesWriterTask( BlockingQueue<OsmDataTarget> queue, OsmBinaryParser parser, List<Osmformat.Node> nodes ) {
            super( queue, parser );
            this.nodes = nodes;
        }

        @Override
        protected void run( OsmDataTarget osmDataTarget ) {
            nodes.stream().map( ( node ) -> {
                long id = node.getId();
                List<OsmTag> tags = new ArrayList<>();
                for ( int i = 0; i < node.getKeysCount(); i++ ) {
                    tags.add( new OsmTag( id, getStringById( node.getKeys( i ) ), getStringById( node.getVals( i ) ), getVersion() ) );
                }
                OsmNode tmp;
                double latf = parseLat( node.getLat() );
                double lonf = parseLon( node.getLon() );

                if ( node.hasInfo() ) {
                    Osmformat.Info info = node.getInfo();
                    tmp = new OsmNode( id, getTimestamp( info ), info.getVersion(), info.getVisible(), info.getChangeset(), info.getUserSid(), tags, latf, lonf, 1 );
                } else {
                    tmp = new OsmNode( id, getTimestamp( null ), getVersion(), getVisible(), getChangeset(), 0, tags, latf, lonf, 1 );
                }
                return tmp;
            } ).forEach( ( tmp ) -> {
                try {
                    osmDataTarget.insertNode( tmp );
                } catch ( IOException ex ) {
                    Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
                }
            } );
        }
    }

    private static class DenseNodesWriterTask extends WriterTask {

        private final Osmformat.DenseNodes nodes;

        public DenseNodesWriterTask( BlockingQueue<OsmDataTarget> queue, OsmBinaryParser parser, Osmformat.DenseNodes nodes ) {
            super( queue, parser );
            this.nodes = nodes;
        }

        @Override
        protected void run( OsmDataTarget osmDataTarget ) {
            long lastId = 0;
            long lastLat = 0;
            long lastLon = 0;
            int keyValsIdx = 0;
            long lasttimestamp = 0, lastchangeset = 0;
            int lastuserSid = 0, lastuid = 0;
            Osmformat.DenseInfo denseInfo = null;
            if ( nodes.hasDenseinfo() ) {
                denseInfo = nodes.getDenseinfo();
            }
            for ( int i = 0; i < nodes.getIdCount(); i++ ) {
                OsmNode tmp;
                List<OsmTag> tags = new ArrayList<>();
                long lat = nodes.getLat( i ) + lastLat;
                lastLat = lat;
                long lon = nodes.getLon( i ) + lastLon;
                lastLon = lon;
                long id = nodes.getId( i ) + lastId;
                lastId = id;
//                System.out.println( "dense node: " + id );
                double latf = parseLat( lat ), lonf = parseLon( lon );
                // If empty, assume that nothing here has keys or vals.
                if ( nodes.getKeysValsCount() > 0 ) {
                    while ( nodes.getKeysVals( keyValsIdx ) != 0 ) {
                        int keyid = nodes.getKeysVals( keyValsIdx++ );
                        int valid = nodes.getKeysVals( keyValsIdx++ );
                        tags.add( new OsmTag( id, getStringById( keyid ), getStringById( valid ), getVersion() ) );
                    }
                    keyValsIdx++; // Skip over the '0' delimiter.
                }
                if ( denseInfo != null ) {
                    int uid = denseInfo.getUid( i ) + lastuid;
                    lastuid = uid;
                    int userSid = denseInfo.getUserSid( i ) + lastuserSid;
                    lastuserSid = userSid;
                    long timestamp = denseInfo.getTimestamp( i ) + lasttimestamp;
                    lasttimestamp = timestamp;
                    int version = denseInfo.getVersion( i );
                    long changeset = denseInfo.getChangeset( i ) + lastchangeset;
                    lastchangeset = changeset;

                    Date date = new Date( getDateGranularity() * timestamp );

                    tmp = new OsmNode( id, new Timestamp( date.getTime() ), version, denseInfo.getVisible( i ), changeset, userSid, tags, latf, lonf, i );
                } else {
                    tmp = new OsmNode( id, getTimestamp( null ), getVersion(), getVisible(), getChangeset(), 0, tags, latf, lonf, i );
                }
                try {
                    osmDataTarget.insertNode( tmp );
                } catch ( IOException ex ) {
                    System.out.println( "exception" );
                    Logger.getLogger( OsmPbfDataSource.class.getName() ).log( Level.SEVERE, null, ex );
                }
//                System.out.println( "" + i + " out of " + nodes.getIdCount() );
            }
            System.out.println( "Done with nodes" );
        }

    }

    private static class TargetThread extends Thread {

        private final OsmDataTarget target;

        public TargetThread( Runnable r, OsmDataTarget target ) {
            super( r );
            this.target = target;
        }

        public OsmDataTarget getTarget() {
            return target;
        }
    }

    private static class TargetThreadFactory implements ThreadFactory {

        private final OsmDataTargetFactory targetFactory;

        public TargetThreadFactory( OsmDataTargetFactory targetFactory ) {
            this.targetFactory = targetFactory;
        }

        @Override
        public Thread newThread( Runnable r ) {
            return new TargetThread( r, targetFactory.createOsmDataTarget() );
        }

    }
}
