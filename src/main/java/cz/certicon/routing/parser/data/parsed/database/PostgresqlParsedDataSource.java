/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed.database;

import cz.certicon.routing.data.basic.database.AbstractServerDatabase;
import cz.certicon.routing.parser.data.parsed.ParsedDataSource;
import cz.certicon.routing.parser.data.parsed.ParsedDataTarget;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdge;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdgeData;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNode;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNodeData;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class PostgresqlParsedDataSource implements ParsedDataSource {

    private final PostgresqlDatabase database;

    public PostgresqlParsedDataSource( Properties connectionProperties ) {
        this.database = new PostgresqlDatabase( connectionProperties );
    }

    @Override
    public void read( ParsedDataTarget target ) throws IOException {
        ResultSet rs;
        target.open();
        try {
            {
                rs = database.read( "SELECT DISTINCT d.id, d.osm_id, d.is_paid, d.length,  e1.speed AS speed_fw, e2.speed AS speed_bw, ST_AsText(d.geom) AS geom FROM (SELECT * FROM edges_routing WHERE is_forward IS TRUE) AS e1 FULL OUTER JOIN (SELECT * FROM edges_routing WHERE is_forward IS FALSE) AS e2 ON e1.data_id = e2.data_id JOIN edges_data_routing d ON (e1.data_id = d.id OR e2.data_id = d.id) ORDER BY d.id;" );
                while ( rs.next() ) {
                    target.insert( new ParsedEdgeData( rs.getLong( "id" ), rs.getLong( "osm_id" ), rs.getBoolean( "is_paid" ), rs.getDouble( "length" ), rs.getInt( "speed_fw" ), rs.getInt( "speed_bw" ), rs.getString( "geom" ) ) );
                }
            }
            {
                rs = database.read( "SELECT * FROM edges_routing;" );
                while ( rs.next() ) {
                    target.insert( new ParsedEdge( rs.getLong( "id" ), rs.getLong( "data_id" ), rs.getBoolean( "is_forward" ), rs.getLong( "source_id" ), rs.getLong( "target_id" ) ) );
                }
            }
            {
                rs = database.read( "SELECT id, osm_id, ST_AsText(geom) AS geom FROM nodes_data_routing;" );
                while ( rs.next() ) {
                    target.insert( new ParsedNodeData( rs.getLong( "id" ), rs.getLong( "osm_id" ), rs.getString( "geom" ) ) );
                }
            }
            {
                rs = database.read( "SELECT * FROM nodes_routing;" );
                while ( rs.next() ) {
                    target.insert( new ParsedNode( rs.getLong( "id" ), rs.getLong( "data_id" ) ) );
                }
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
        target.close();
    }

    private static class PostgresqlDatabase extends AbstractServerDatabase<ResultSet, String> {

        public PostgresqlDatabase( Properties connectionProperties ) {
            super( connectionProperties );
        }

        @Override
        protected ResultSet checkedRead( String ad ) throws SQLException {
            return getStatement().executeQuery( ad );
        }

        @Override
        protected void checkedWrite( ResultSet entity ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
