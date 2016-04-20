/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.database;

import cz.certicon.routing.data.basic.database.AbstractDatabase;
import cz.certicon.routing.parser.data.OsmDataTarget;
import cz.certicon.routing.parser.model.entity.osm.OsmNode;
import cz.certicon.routing.parser.model.entity.osm.OsmRelation;
import cz.certicon.routing.parser.model.entity.osm.OsmWay;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

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
        waysStringBuilder.append("");
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insertNode( OsmNode node ) throws IOException {
        // INSERT INTO nodes(node_id, timestamp, version, visible, changeset_id, latitude, longitude, tile)
        // INSERT INTO node_tags (node_id, k, v, version)
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insertRelation( OsmRelation relation ) throws IOException {
        // INSERT INTO relations (relation_id, timestamp, version, visible, changeset_id)
        // INSERT INTO relation_tags (relation_id, k, v, version)
        // INSERT INTO relation_members (relation_id, member_type, member_id, sequence_id, member_role, version)
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void open() throws IOException {
        db.open();
    }

    @Override
    public void close() throws IOException {
        db.close();
    }

    private static class PostrgresqlOsmDatabase extends AbstractDatabase<String, String> {

        public PostrgresqlOsmDatabase( Properties connectionProperties ) {
            super( connectionProperties );
        }

        @Override
        protected String checkedRead( String ad ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void checkedWrite( String entity ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
