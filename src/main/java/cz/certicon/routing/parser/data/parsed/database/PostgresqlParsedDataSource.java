/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed.database;

import cz.certicon.routing.data.basic.database.AbstractServerDatabase;
import cz.certicon.routing.parser.data.osm.OsmDataSource;
import cz.certicon.routing.parser.data.osm.OsmDataTarget;
import cz.certicon.routing.parser.data.osm.OsmDataTargetFactory;
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

    private final PostrgresqlOsmDatabase database;

    public PostgresqlParsedDataSource( Properties connectionProperties ) {
        this.database = new PostrgresqlOsmDatabase( connectionProperties );
    }

    @Override
    public void read( ParsedDataTarget target ) throws IOException {
        ResultSet rs;
        try {
            {
                rs = database.read( "SELECT DISTINCT data_id, osm_id, is_paid, length, is_forward, speed, ST_Text(geom) FROM edges_view ORDER BY data_id;" );
                long id = -1;
                int speedFw = -1;
                int speedBw = -1;
                while ( rs.next() ) {
                    boolean isForward = rs.getBoolean( "is_forward" );
                    if ( id != -1 ) {
                        if ( ( id != rs.getLong( "data_id" ) ) || ( isForward && speedFw != -1 ) || ( speedFw == -1 && speedBw == -1 ) ) {
                            throw new AssertionError( "Wrong SQL query" );
                        }
                        if ( isForward ) {
                            speedFw = rs.getInt( "speed" );
                        } else {
                            speedBw = rs.getInt( "speed" );
                        }
                        target.insert( new ParsedEdgeData( id, rs.getLong( "osm_id" ), rs.getBoolean( "is_paid" ), rs.getDouble( "length" ), speedFw, speedBw, rs.getString( "geom" ) ) );
                        id = speedFw = speedBw = -1;
                    } else {
                        id = rs.getLong( "data_id" );
                        if ( isForward ) {
                            speedFw = rs.getInt( "speed" );
                        } else {
                            speedBw = rs.getInt( "speed" );
                        }
                    }
                }
            }
            {
                rs = database.read( "SELECT * FROM edges_routing;" );
                while ( rs.next() ) {
                    target.insert( new ParsedEdge( rs.getLong( "id" ), rs.getLong( "data_id" ), rs.getBoolean( "is_forward" ), rs.getLong( "source_id" ), rs.getLong( "target_id" ) ) );
                }
            }
            {
                rs = database.read( "SELECT id, osm_id, ST_AsText(geom) FROM nodes_data_routing;" );
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
    }

    private static class PostrgresqlOsmDatabase extends AbstractServerDatabase<ResultSet, String> {

        public PostrgresqlOsmDatabase( Properties connectionProperties ) {
            super( connectionProperties );
        }

        @Override
        protected ResultSet checkedRead( String ad ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void checkedWrite( ResultSet entity ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
