/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.pbf;

import cz.certicon.routing.data.DataSource;
import cz.certicon.routing.parser.data.OsmDataSource;
import cz.certicon.routing.parser.data.OsmDataTarget;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import cz.certicon.routing.utils.measuring.TimeUnits;
import java.io.IOException;
import java.util.List;
import org.openstreetmap.osmosis.osmbinary.BinaryParser;
import org.openstreetmap.osmosis.osmbinary.Osmformat;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class OsmPbfDataSource implements OsmDataSource {

    private static final double SPEED_EPS = 10E-2;

    private final DataSource dataSource;

    public OsmPbfDataSource( DataSource dataSource ) throws IOException {
        this.dataSource = dataSource;
    }

    @Override
    public boolean read( OsmDataTarget target ) throws IOException {
//        OsmBinaryParser brad = new OsmBinaryParser( graphEntityFactory, distanceFactory, graphLoadListener );
//        BlockInputStream blockInputStream = new BlockInputStream( new BufferedInputStream( dataSource.getInputStream() ), brad );
//        blockInputStream.process();
        return false;
    }

    private class OsmBinaryParser extends BinaryParser {

        private TimeMeasurement time;

        public OsmBinaryParser() {
            this.time = new TimeMeasurement();
            this.time.setTimeUnits( TimeUnits.MILLISECONDS );
            this.time.start();
        }

        @Override
        protected void parseRelations( List<Osmformat.Relation> list ) {
        }

        @Override
        protected void parseDense( Osmformat.DenseNodes nodes ) {
            long lastId = 0;
            long lastLat = 0;
            long lastLon = 0;
            for ( int i = 0; i < nodes.getIdCount(); i++ ) {
                lastId += nodes.getId( i );
                lastLat += nodes.getLat( i );
                lastLon += nodes.getLon( i );
            }
        }

        @Override
        protected void parseNodes( List<Osmformat.Node> nodes ) {
            for ( Osmformat.Node node : nodes ) {
            }
        }

        @Override
        protected void parseWays( List<Osmformat.Way> ways ) {
            for ( Osmformat.Way way : ways ) {
                long lastRef = 0;
                for ( Long ref : way.getRefsList() ) {
                    lastRef += ref;
                }
            }
        }

        @Override
        protected void parse( Osmformat.HeaderBlock hb ) {
//            System.out.println( "Got header block." );
        }

        @Override
        public void complete() {
            System.out.println( "Complete reading in time " + time.stop() + " ms!" );
        }

    }
}
