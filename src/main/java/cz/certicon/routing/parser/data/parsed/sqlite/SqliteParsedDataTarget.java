/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed.sqlite;

import cz.certicon.routing.data.basic.database.AbstractEmbeddedDatabase;
import cz.certicon.routing.parser.data.parsed.ParsedDataTarget;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdge;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdgeData;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNode;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNodeData;
import cz.certicon.routing.parser.model.entity.parsed.ParsedTurnRestriction;
import cz.certicon.routing.parser.model.entity.parsed.ParsedTurnRestrictionArray;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.sqlite.SQLiteConfig;

/**
 * Properties requirements (and example values): driver=org.sqlite.JDBC
 * url=jdbc:sqlite:spatialite.sample
 * spatialite_path=/usr/local/lib/libspatialite.so
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SqliteParsedDataTarget implements ParsedDataTarget {

    private final StringDatabase database;
    private PreparedStatement edgeDataStatement = null;
    private int edgeDataCounter = 0;
    private PreparedStatement edgeStatement = null;
    private int edgeCounter = 0;
    private PreparedStatement nodeDataStatement = null;
    private int nodeDataCounter = 0;
    private PreparedStatement nodeStatement = null;
    private int nodeCounter = 0;
    private PreparedStatement turnRestrictionStatement = null;
    private int turnRestrictionCounter = 0;
    private PreparedStatement turnRestrictionArrayStatement = null;
    private int turnRestrictionArrayCounter = 0;
    private boolean isOpen = false;
    private final Properties executionProperties;
    private int batchSize;

    public SqliteParsedDataTarget( Properties properties ) throws IOException {
        this.database = new StringDatabase( properties );
        this.executionProperties = new Properties();
        this.executionProperties.setProperty( "batch_size", "200" );
        this.executionProperties.setProperty( "index", "true" );
    }

    @Override
    public void open() throws IOException {
        if ( !isOpen ) {
            database.setExecutionProperties( executionProperties );
            batchSize = Integer.parseInt( executionProperties.getProperty( "batch_size" ) );
            database.init();
            isOpen = true;
        }
    }

    @Override
    public void insert( ParsedEdgeData edgeData ) throws IOException {
        open();
        try {
            if ( edgeDataStatement == null ) {
                edgeDataStatement = database.prepareStatement( "INSERT INTO edges_data (id, osm_id, is_paid, length, speed_fw, speed_bw, geom) VALUES (?, ?, ?, ?, ?, ?, GeomFromText(?, 4326))" );
            }
            int idx = 1;
            edgeDataStatement.setLong( idx++, edgeData.getId() );
            edgeDataStatement.setLong( idx++, edgeData.getOsmId() );
            edgeDataStatement.setInt( idx++, edgeData.isIsPaid() ? 1 : 0 );
            edgeDataStatement.setDouble( idx++, edgeData.getLength() );
            edgeDataStatement.setInt( idx++, edgeData.getSpeedForward() );
            edgeDataStatement.setInt( idx++, edgeData.getSpeedBackward() );
            edgeDataStatement.setString( idx++, edgeData.getGeometry() );
            edgeDataStatement.addBatch();
            if ( ++edgeDataCounter % batchSize == 0 ) {
                edgeDataStatement.executeBatch();
            }

//            database.checkedWrite( "INSERT INTO edges_data (id, osm_id, is_paid, length, speed_fw, speed_bw, geom) VALUES ("
//                    + edgeData.getId() + ", " + edgeData.getOsmId() + ", " + ( edgeData.isIsPaid() ? 1 : 0 ) + ", " + edgeData.getLength() + ", " + edgeData.getSpeedForward() + ", " + edgeData.getSpeedBackward() + ", " + "ST_GeomFromText('" + edgeData.getGeometry() + "',4326)" + ")" );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( ParsedEdge edge ) throws IOException {
        open();
        try {
            if ( edgeStatement == null ) {
                edgeStatement = database.prepareStatement( "INSERT INTO edges (id, data_id, is_forward, source_id, target_id) VALUES (?, ?, ?, ?, ?)" );
            }
            int idx = 1;
            edgeStatement.setLong( idx++, edge.getId() );
            edgeStatement.setLong( idx++, edge.getDataId() );
            edgeStatement.setInt( idx++, edge.isIsForward() ? 1 : 0 );
            edgeStatement.setLong( idx++, edge.getSourceId() );
            edgeStatement.setLong( idx++, edge.getTargetId() );
            edgeStatement.addBatch();
            if ( ++edgeCounter % batchSize == 0 ) {
                edgeStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( ParsedNodeData nodeData ) throws IOException {
        open();
        try {
            if ( nodeDataStatement == null ) {
                nodeDataStatement = database.prepareStatement( "INSERT INTO nodes_data (id, osm_id, geom) VALUES (?, ?, GeomFromText(?,4326))" );
            }
            int idx = 1;
            nodeDataStatement.setLong( idx++, nodeData.getId() );
            nodeDataStatement.setLong( idx++, nodeData.getOsmId() );
            nodeDataStatement.setString( idx++, nodeData.getGeom() );
            nodeDataStatement.addBatch();
            if ( ++nodeDataCounter % batchSize == 0 ) {
                nodeDataStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( ParsedNode node ) throws IOException {
        open();
        try {
            if ( nodeStatement == null ) {
                nodeStatement = database.prepareStatement( "INSERT INTO nodes (id, data_id) VALUES (?, ?)" );
            }
            int idx = 1;
            nodeStatement.setLong( idx++, node.getId() );
            nodeStatement.setLong( idx++, node.getDataId() );
            nodeStatement.addBatch();
            if ( ++nodeCounter % batchSize == 0 ) {
                nodeStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( ParsedTurnRestriction turnRestriction ) throws IOException {
        open();
        try {
            if ( turnRestrictionStatement == null ) {
                turnRestrictionStatement = database.prepareStatement( "INSERT INTO turn_restrictions (from_id, via_id, to_id) VALUES (?, ?, ?)" );
            }
            int idx = 1;
            turnRestrictionStatement.setLong( idx++, turnRestriction.getFromId() );
            turnRestrictionStatement.setLong( idx++, turnRestriction.getViaId() );
            turnRestrictionStatement.setLong( idx++, turnRestriction.getToId() );
            turnRestrictionStatement.addBatch();
            if ( ++turnRestrictionCounter % batchSize == 0 ) {
                turnRestrictionStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( ParsedTurnRestrictionArray turnRestrictionArray ) throws IOException {
        open();
        try {
            if ( turnRestrictionArrayStatement == null ) {
                turnRestrictionArrayStatement = database.prepareStatement( "INSERT INTO turn_restrictions_array (array_id, position, edge_id) VALUES (?, ?, ?)" );
            }
            int idx = 1;
            turnRestrictionArrayStatement.setLong( idx++, turnRestrictionArray.getArrayId() );
            turnRestrictionArrayStatement.setInt( idx++, turnRestrictionArray.getPosition());
            turnRestrictionArrayStatement.setLong( idx++, turnRestrictionArray.getEdgeId() );
            turnRestrictionArrayStatement.addBatch();
            if ( ++turnRestrictionArrayCounter % batchSize == 0 ) {
                turnRestrictionArrayStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }


    @Override
    public void close() throws IOException {
        try {
            edgeDataStatement.executeBatch();
            edgeStatement.executeBatch();
            nodeDataStatement.executeBatch();
            nodeStatement.executeBatch();
            database.finish();
            database.close();
            isOpen = false;
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void setExecutionProperties( Properties properties ) {
        properties.entrySet().stream().forEach( ( entry ) -> {
            executionProperties.put( entry.getKey(), entry.getValue() );
        } );
    }
    private static class StringDatabase extends AbstractEmbeddedDatabase<String, String> {

        private final Properties properties;
        private Properties executionProperties;

        public StringDatabase( Properties connectionProperties ) {
            super( connectionProperties );
            this.properties = connectionProperties;
            SQLiteConfig config = new SQLiteConfig();
            config.enableLoadExtension( true );
            for ( Map.Entry<Object, Object> entry : config.toProperties().entrySet() ) {
                connectionProperties.put( entry.getKey(), entry.getValue() );
            }
        }

        public void setExecutionProperties( Properties executionProperties ) {
            this.executionProperties = executionProperties;
        }

        @Override
        protected String checkedRead( String in ) throws SQLException {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void checkedWrite( String in ) throws SQLException {
            getStatement().execute( in );
        }

        public PreparedStatement prepareStatement( String statement ) throws SQLException, IOException {
            if ( !isOpen() ) {
                open();
            }
            return getConnection().prepareStatement( statement );
        }

        public void init() throws IOException {
            if ( !isOpen() ) {
                open();
            }
            try {
                // initialize SpatiaLite
                getConnection().setAutoCommit( false ); //transaction block start
                getStatement().execute( "SELECT load_extension('" + properties.getProperty( "spatialite_path" ) + "')" );
                getStatement().execute( "SELECT InitSpatialMetadata('WGS84_ONLY')" );
                // create tables
                getStatement().execute( "CREATE TABLE edges_data ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "osm_id INTEGER,"
                        + "is_paid INTEGER,"
                        + "length REAL,"
                        + "speed_fw INTEGER,"
                        + "speed_bw INTEGER"
                        + ")" );
                getStatement().execute( "SELECT AddGeometryColumn('edges_data','geom',4326,'LINESTRING','XY')" );
                getStatement().execute( "CREATE TABLE edges ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "data_id INTEGER,"
                        + "is_forward INTEGER,"
                        + "source_id INTEGER,"
                        + "target_id INTEGER"
                        + ")" );
                getStatement().execute( "CREATE TABLE nodes_data ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "osm_id INTEGER"
                        + ")" );
                getStatement().execute( "SELECT AddGeometryColumn('nodes_data','geom',4326,'POINT','XY')" );
                getStatement().execute( "CREATE TABLE nodes ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "data_id INTEGER"
                        + ")" );
                getStatement().execute( "CREATE TABLE turn_restrictions ("
                        + "from_id INTEGER,"
                        + "via_id INTEGER,"
                        + "to_id INTEGER"
                        + ")" );
                getStatement().execute( "CREATE TABLE turn_restrictions_array ("
                        + "array_id INTEGER,"
                        + "position INTEGER,"
                        + "edge_id INTEGER"
                        + ")" );
            } catch ( SQLException ex ) {
                throw new IOException( ex );
            }
        }

        public void finish() throws IOException {
            try {
                // create indexes
                if ( "true".equals( executionProperties.getProperty( "index" ) ) ) {
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_edges_data` ON `edges_data` (`id` ASC)" );
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_edges` ON `edges` (`id` ASC)" );
                    getStatement().execute( "CREATE INDEX `fk_id_edges_data` ON `edges` (`data_id` ASC)" );
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_nodes_data` ON `nodes_data` (`id` ASC)" );
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_nodes` ON `nodes` (`id` ASC)" );
                    getStatement().execute( "CREATE INDEX `fk_id_nodes_data` ON `nodes` (`data_id` ASC)" );
                    getStatement().execute( "CREATE INDEX `idx_id_via` ON `turn_restrictions` (`via_id` ASC)" );
                    getStatement().execute( "CREATE INDEX `idx_id_array` ON `turn_restrictions_array` (`array_id` ASC)" );
                    getStatement().execute( "CREATE INDEX `idx_position` ON `turn_restrictions_array` (`position` ASC)" );
                    getStatement().execute( "SELECT CreateSpatialIndex('edges_data','geom')" );
                    getStatement().execute( "SELECT CreateSpatialIndex('nodes_data','geom')" );
                }
                // finish
                getConnection().commit();
                getConnection().setAutoCommit( true ); //transaction block end
            } catch ( SQLException ex ) {
                throw new IOException( ex );
            }
        }

    }
}
