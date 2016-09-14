/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.sara;

import cz.certicon.routing.data.basic.database.AbstractEmbeddedDatabase;
import cz.certicon.routing.parser.model.entity.sra.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.sqlite.SQLiteConfig;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SqliteDataTarget implements DataTarget {

    private final StringDatabase database;
    private PreparedStatement edgeStatement = null;
    private int edgeCounter = 0;
    private PreparedStatement nodeStatement = null;
    private int nodeCounter = 0;
    private PreparedStatement turnTableStatement = null;
    private int turnTableCounter = 0;
    private PreparedStatement turnTableValueStatement = null;
    private int turnTableValueCounter = 0;
    private boolean isOpen = false;
    private final Properties executionProperties;
    private int batchSize;

    public SqliteDataTarget( Properties properties ) throws IOException {
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
    public void insert( Edge edge ) throws IOException {
        open();
        try {
            if ( edgeStatement == null ) {
                edgeStatement = database.prepareStatement( "INSERT INTO edges (id, source, target, source_pos, target_pos, oneway, paid, metric_length, metric_speed_forward, metric_speed_backward, geom) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GeomFromText(?, 4326))" );
            }
            int idx = 1;
            edgeStatement.setLong( idx++, edge.getId() );
            edgeStatement.setLong( idx++, edge.getSource() );
            edgeStatement.setLong( idx++, edge.getTarget() );
            edgeStatement.setLong( idx++, edge.getSourcePosition());
            edgeStatement.setLong( idx++, edge.getTargetPosition() );
            edgeStatement.setInt( idx++, edge.isOneway() ? 1 : 0 );
            edgeStatement.setInt( idx++, edge.isPaid() ? 1 : 0 );
            edgeStatement.setDouble( idx++, edge.getLength() );
            edgeStatement.setDouble( idx++, edge.getSpeedForward() );
            edgeStatement.setDouble( idx++, edge.getSpeedBackward() );
            edgeStatement.setString( idx++, edge.getGeometry() );
            edgeStatement.addBatch();
            if ( ++edgeCounter % batchSize == 0 ) {
                edgeStatement.executeBatch();
            }

//            database.checkedWrite( "INSERT INTO edges_data (id, osm_id, is_paid, length, speed_fw, speed_bw, geom) VALUES ("
//                    + edgeData.getId() + ", " + edgeData.getOsmId() + ", " + ( edgeData.isIsPaid() ? 1 : 0 ) + ", " + edgeData.getLength() + ", " + edgeData.getSpeedForward() + ", " + edgeData.getSpeedBackward() + ", " + "ST_GeomFromText('" + edgeData.getGeometry() + "',4326)" + ")" );
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( Node node ) throws IOException {
        open();
        try {
            if ( nodeStatement == null ) {
                nodeStatement = database.prepareStatement( "INSERT INTO nodes (id, turn_table_id, geom) VALUES (?, ?, GeomFromText(?,4326))" );
            }
            int idx = 1;
            nodeStatement.setLong( idx++, node.getId() );
            nodeStatement.setInt( idx++, node.getTurnTableId() );
            nodeStatement.setString( idx++, node.getGeometry() );
            nodeStatement.addBatch();
            if ( ++nodeCounter % batchSize == 0 ) {
                nodeStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( TurnTable turnTable ) throws IOException {
        open();
        try {
            if ( turnTableStatement == null ) {
                turnTableStatement = database.prepareStatement( "INSERT INTO turn_tables (id, size) VALUES (?, ?)" );
            }
            int idx = 1;
            turnTableStatement.setInt( idx++, turnTable.getId() );
            turnTableStatement.setInt( idx++, turnTable.getSize() );
            turnTableStatement.addBatch();
            if ( ++turnTableCounter % batchSize == 0 ) {
                turnTableStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void insert( TurnTableValue turnTableValue ) throws IOException {
        open();
        try {
            if ( turnTableValueStatement == null ) {
                turnTableValueStatement = database.prepareStatement( "INSERT INTO turn_table_values (turn_table_id, row_id, column_id, value) VALUES (?, ?, ?, ?)" );
            }
            int idx = 1;
            turnTableValueStatement.setInt( idx++, turnTableValue.getTurnTableId() );
            turnTableValueStatement.setInt( idx++, turnTableValue.getRowId() );
            turnTableValueStatement.setInt( idx++, turnTableValue.getColumnId() );
            turnTableValueStatement.setDouble( idx++, turnTableValue.getValue() );
            turnTableValueStatement.addBatch();
            if ( ++turnTableValueCounter % batchSize == 0 ) {
                turnTableValueStatement.executeBatch();
            }
        } catch ( SQLException ex ) {
            throw new IOException( ex );
        }
    }

    @Override
    public void close() throws IOException {
        try {
            edgeStatement.executeBatch();
            nodeStatement.executeBatch();
            turnTableStatement.executeBatch();
            turnTableValueStatement.executeBatch();
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
                getStatement().execute( "CREATE TABLE edges ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "source INTEGER,"
                        + "target INTEGER,"
                        + "source_pos INTEGER,"
                        + "target_pos INTEGER,"
                        + "oneway INTEGER,"
                        + "paid INTEGER,"
                        + "metric_length REAL,"
                        + "metric_speed_forward REAL,"
                        + "metric_speed_backward REAL"
                        + ")" );
                getStatement().execute( "SELECT AddGeometryColumn('edges','geom',4326,'LINESTRING','XY')" );
                getStatement().execute( "CREATE TABLE nodes ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "turn_table_id INTEGER"
                        + ")" );
                getStatement().execute( "SELECT AddGeometryColumn('nodes','geom',4326,'POINT','XY')" );
                getStatement().execute( "CREATE TABLE turn_tables ("
                        + "id INTEGER NOT NULL PRIMARY KEY,"
                        + "size INTEGER"
                        + ")" );
                getStatement().execute( "CREATE TABLE turn_table_values ("
                        + "turn_table_id INTEGER,"
                        + "row_id INTEGER,"
                        + "column_id INTEGER,"
                        + "value REAL"
                        + ")" );
            } catch ( SQLException ex ) {
                throw new IOException( ex );
            }
        }

        public void finish() throws IOException {
            try {
                // create indexes
                if ( "true".equals( executionProperties.getProperty( "index" ) ) ) {
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_edges` ON `edges` (`id` ASC)" );
                    getStatement().execute( "CREATE UNIQUE INDEX `idx_id_nodes` ON `nodes` (`id` ASC)" );
                    getStatement().execute( "SELECT CreateSpatialIndex('edges','geom')" );
                    getStatement().execute( "SELECT CreateSpatialIndex('nodes','geom')" );
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
