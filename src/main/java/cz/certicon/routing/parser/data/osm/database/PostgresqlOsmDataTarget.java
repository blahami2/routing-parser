/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.osm.database;

import cz.certicon.routing.data.basic.database.AbstractDatabase;
import cz.certicon.routing.data.basic.database.AbstractServerDatabase;
import cz.certicon.routing.model.basic.Pair;
import cz.certicon.routing.parser.data.osm.OsmDataTarget;
import cz.certicon.routing.parser.model.entity.osm.*;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class PostgresqlOsmDataTarget implements OsmDataTarget {

    private static final int BULK_SIZE = 200;
    private final PostrgresqlOsmDatabase db;
    private final StringBuilder waysStringBuilder = new StringBuilder();
    private int waysCounter = 0;
    private final StringBuilder nodesStringBuilder = new StringBuilder();
    private int nodesCounter = 0;
    private final StringBuilder relationsStringBuilder = new StringBuilder();
    private int relationsCounter = 0;

    public PostgresqlOsmDataTarget( Properties connectionProperties ) {
        db = new PostrgresqlOsmDatabase( connectionProperties );
    }

    @Override
    public boolean insertWay( OsmWay way ) throws IOException {
        // INSERT INTO ways (way_id, timestamp, version, visible, changeset_id)
        // INSERT INTO way_tags (way_id, k, v, version)
        // INSERT INTO way_nodes (way_id, node_id, sequence_id, version)
        appendOsmEntity( waysStringBuilder, way );
        waysStringBuilder.append( ",'{" );
        for ( OsmWayNode node : way.getNodes() ) {
            waysStringBuilder.append( node.getNodeId() ).append( "," );
        }
        waysStringBuilder.delete( waysStringBuilder.length() - 1, waysStringBuilder.length() );
        waysStringBuilder.append( "}'\n" );
        if ( waysCounter++ % BULK_SIZE == 0 ) {
            db.write( new Pair<>( "ways", waysStringBuilder.toString() ) );
            waysStringBuilder.delete( 0, waysStringBuilder.length() );
        }
        return true;
    }

    @Override
    public boolean insertNode( OsmNode node ) throws IOException {
        // INSERT INTO nodes(node_id, timestamp, version, visible, changeset_id, latitude, longitude, tile)
        // INSERT INTO node_tags (node_id, k, v, version)
//        System.out.println( "inserting node" );
        return false;
    }

    @Override
    public boolean insertRelation( OsmRelation relation ) throws IOException {
        // INSERT INTO relations (relation_id, timestamp, version, visible, changeset_id)
        // INSERT INTO relation_tags (relation_id, k, v, version)
        // INSERT INTO relation_members (relation_id, member_type, member_id, sequence_id, member_role, version)
//        System.out.println( "inserting relation" );
        return false;
    }

    private void appendOsmEntity( StringBuilder sb, OsmEntity osmEntity ) {
        /*CREATE TABLE ways (
    id bigint NOT NULL,
    version int NOT NULL,
    user_id int NOT NULL,
    tstamp timestamp without time zone NOT NULL,
    changeset_id bigint NOT NULL,
    tags hstore,
    nodes bigint[]
);*/
        sb.append( osmEntity.getId() ).append( "," )
                .append( osmEntity.getVersion() ).append( "," )
                .append( osmEntity.getUserId() ).append( "," )
                .append( "'to_timestamp(" ).append( osmEntity.getTimestamp().getTime() ).append( ")" ).append( "'," )
                .append( osmEntity.getChangeSetId() ).append( "," );
        appendTags( sb, osmEntity.getTags() );
    }

    @Override
    public void open() throws IOException {
        db.open();
    }

    @Override
    public void close() throws IOException {
        db.close();
    }

    private void appendTags( StringBuilder sb, List<OsmTag> tags ) {
        /* '"milk"=>"4", 
                     "bread"=>"2", 
                     "bananas"=>"12", 
                     "cereal"=>"1"'*/
        sb.append( "'" );
        tags.stream().forEach( ( tag ) -> {
            sb.append( "\"" ).append( tag.getKey() ).append( "\"=>\"" ).append( tag.getValue() ).append( "\"," );
        } );
        sb.delete( sb.length() - 1, sb.length() );
        sb.append( "'" );

    }

    private static class PostrgresqlOsmDatabase extends AbstractServerDatabase<Pair<String, String>, String> {

        private CopyManager copyManager = null;

        public PostrgresqlOsmDatabase( Properties connectionProperties ) {
            super( connectionProperties );
        }

        @Override
        protected Pair<String, String> checkedRead( String ad ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void checkedWrite( Pair<String, String> entity ) throws SQLException {
            Reader reader = new StringReader( entity.b );
//            System.out.println( entity.b );
            try {
                getCopyManager().copyIn( "COPY " + entity.a + " FROM STDIN WITH CSV", reader );
            } catch ( IOException ex ) {
                throw new SQLException( ex );
            }
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        private CopyManager getCopyManager() throws SQLException {
            if ( copyManager == null ) {
                copyManager = ( (PGConnection) getConnection() ).getCopyAPI();
            }
            return copyManager;
        }

    }

}
