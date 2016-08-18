/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.sra;

import cz.certicon.routing.data.basic.database.AbstractServerDatabase;
import cz.certicon.routing.model.basic.Trinity;
import cz.certicon.routing.parser.model.entity.sra.*;
import cz.certicon.routing.parser.utils.Matrix;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class PostgreDataSource implements DataSource {

    private final PostgresqlDatabase database;
    private final Properties executionProperties = new Properties();

    public PostgreDataSource( Properties connectionProperties ) {
        this.database = new PostgresqlDatabase( connectionProperties );
    }

    @Override
    public void read( DataTarget target ) throws IOException {
        ResultSet rs;
        target.open();
        try {
            TLongObjectMap<Node> nodeMap = new TLongObjectHashMap<>();
            TLongObjectMap<Matrix<Long, Double>> matrixMap = new TLongObjectHashMap<>();
            // load nodes and create matrix for each node
            rs = database.read( "SELECT n.id, ST_AsText(d.geom) AS geom FROM nodes_routing n JOIN nodes_data_routing d ON n.id = d.data_id;" );
            while ( rs.next() ) {
                Node node = new Node( rs.getLong( "id" ), -1, rs.getString( "geom" ) );
                matrixMap.put( node.getId(), new Matrix<>() );
                // insert nodes into database LATER, here is the wrong turntableid
                nodeMap.put( node.getId(), node );
//                target.insert( node );
            }
            // load edges 
            rs = database.read( "SELECT DISTINCT d.id, d.is_paid, d.length,  e1.speed AS speed_fw, e2.speed AS speed_bw, ST_AsText(d.geom) AS geom, e1.source_id, e1.target_id "
                    + "FROM (SELECT * FROM edges_routing WHERE is_forward IS TRUE) AS e1 "
                    + "FULL OUTER JOIN (SELECT * FROM edges_routing WHERE is_forward IS FALSE) AS e2 ON e1.data_id = e2.data_id "
                    + "JOIN edges_data_routing d ON (e1.data_id = d.id OR e2.data_id = d.id) "
                    + "ORDER BY d.id;" );
            while ( rs.next() ) {
                Edge edge = new Edge( rs.getLong( "id" ), rs.getLong( "source_id" ), rs.getLong( "target_id" ),
                        rs.getInt( "speed_bw" ) == 0, rs.getBoolean( "is_paid" ),
                        rs.getDouble( "length" ), rs.getInt( "speed_fw" ), rs.getInt( "speed_bw" ), rs.getString( "geom" ) );
                // add edge to nodes' matrices
                Matrix<Long, Double> sourceMatrix = matrixMap.get( edge.getSource() );
                sourceMatrix.set( edge.getId(), edge.getId(), Double.MAX_VALUE );
                Matrix<Long, Double> targetMatrix = matrixMap.get( edge.getTarget() );
                targetMatrix.set( edge.getId(), edge.getId(), Double.MAX_VALUE );
                // insert edges into database
                target.insert( edge );
            }
            // for each node
            TLongObjectIterator<Matrix<Long, Double>> iterator = matrixMap.iterator();
            while ( iterator.hasNext() ) {
                long nodeId = iterator.key();
                // get edge keys (edges) and orders
                Matrix<Long, Double> matrix = iterator.value();
                for ( Long rowKey : matrix.getRowKeys() ) {
                    int order = matrix.getRowKeyPosition( rowKey );
                    NodeToEdge nodeToEdge = new NodeToEdge( nodeId, rowKey, order );
                    // insert node, edge_key, edge_id into database
                    target.insert( nodeToEdge );
                }
                iterator.advance();
            }
            // read turn restrictions and insert them into matrices
            TLongObjectMap<Trinity<TLongList, Long, Long>> trMap = new TLongObjectHashMap<>();
            // - create map for trs
            rs = database.read( "SELECT * FROM turn_restrictions;" );
            while ( rs.next() ) {
                trMap.put( rs.getLong( "from_id" ), new Trinity<>( new TLongArrayList(), rs.getLong( "via_id" ), rs.getLong( "to_id" ) ) );
            }
            // - add edges
            rs = database.read( "SELECT * FROM turn_restrictions_array ORDER BY position;" );
            while ( rs.next() ) {
                Trinity<TLongList, Long, Long> trinity = trMap.get( rs.getLong( "array_id" ) );
                trinity.a.add( rs.getLong( "edge_id" ) );
            }
            // - take last edge and to_edge and add them into matrix given by via
            for ( Object o : trMap.values() ) {
                Trinity<TLongList, Long, Long> trinity = (Trinity<TLongList, Long, Long>) o;
                long fromId = trinity.a.get( trinity.a.size() - 1 );
                long nodeId = trinity.b;
                long toId = trinity.c;
                Matrix<Long, Double> matrix = matrixMap.get( nodeId );
                matrix.set( fromId, toId, Double.MAX_VALUE );
            }
            // create map for matrices<Matrix, List<Node.Id>>
            Map<Matrix<Long, Double>, TLongList> turnTableMap = new HashMap<>();
            // fill the map with matrices
            iterator = matrixMap.iterator();
            while ( iterator.hasNext() ) {
                TLongList nodeList;
                // - if the map does not contain matrix, insert matrix and create a new list, add node to the list
                if ( !turnTableMap.containsKey( iterator.value() ) ) {
                    nodeList = new TLongArrayList();
                    turnTableMap.put( iterator.value(), nodeList );
                } else {
                    // - otherwise just add node to the existing list
                    nodeList = turnTableMap.get( iterator.value() );
                }
                nodeList.add( iterator.key() );
                iterator.advance();
            }
            // foreach matrix in the map insert turntable and its nodes
            int ttCounter = 1;
            for ( Map.Entry<Matrix<Long, Double>, TLongList> entry : turnTableMap.entrySet() ) {
                Matrix<Long, Double> matrix = entry.getKey();
                if ( matrix.getRowCount() != matrix.getColumnCount() ) {
                    throw new AssertionError( "Not a square matrix: " + matrix );
                }
                TLongList nodes = entry.getValue();
                TurnTable turnTable = new TurnTable( ttCounter, matrix.getRowCount() );
                target.insert( turnTable );
                // insert nodes
                TLongIterator nodeIterator = nodes.iterator();
                while ( nodeIterator.hasNext() ) {
                    long nodeId = nodeIterator.next();
                    Node node = nodeMap.get( nodeId );
                    target.insert( node.withTurnTableId( ttCounter ) );
                }
                // and foreach value insert value
                for ( int i = 0; i < matrix.getRowCount(); i++ ) {
                    for ( int j = 0; j < matrix.getColumnCount(); j++ ) {
                        target.insert( new TurnTableValue( ttCounter, i, j, matrix.get( i, j ) ) );
                    }
                }
                ttCounter++;
            }
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
