/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.utils;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 * @param <K> key type
 * @param <E> element type
 */
@ToString
@EqualsAndHashCode( exclude = { "rowMap", "columnMap" } )
public class Matrix<K, E> {

    private final TObjectIntMap<K> rowMap = new TObjectIntHashMap<>();
    private final TObjectIntMap<K> columnMap = new TObjectIntHashMap<>();
    private final List<List<E>> matrix = new ArrayList<>();
    private final E initValue;

    public Matrix() {
        this.initValue = null;
    }

    public Matrix( E initValue ) {
        this.initValue = initValue;
    }

    public int getRowCount() {
        return matrix.size();
    }

    public int getColumnCount() {
        if ( matrix.isEmpty() ) {
            return 0;
        }
        return matrix.get( 0 ).size();
    }

    public int getRowKeyPosition( K rowKey ) {
        checkKey( rowMap, rowKey );
        return rowMap.get( rowKey );
    }

    public int getColumnKeyPosition( K columnKey ) {
        checkKey( columnMap, columnKey );
        return columnMap.get( columnKey );
    }

    public Set<K> getRowKeys() {
        return rowMap.keySet();
    }

    public Set<K> getColumnKeys() {
        return columnMap.keySet();
    }

    public List<E> getRow( K rowKey ) {
        checkKey( rowMap, rowKey );
        return new ArrayList<>( matrix.get( getRowKeyPosition( rowKey ) ) );
    }

    public List<E> getColumn( K columnKey ) {
        checkKey( columnMap, columnKey );
        List<E> column = new ArrayList<>();
        matrix.stream().forEach( ( list ) -> {
            column.add( list.get( getColumnKeyPosition( columnKey ) ) );
        } );
        return column;
    }

    public void set( K rowKey, K columnKey, E element ) {
        int rowPos;
        List<E> row;
        if ( !rowMap.containsKey( rowKey ) ) {
            rowPos = getRowCount();
            row = new ArrayList<>();
            matrix.add( row );
            for ( int i = 0; i < getColumnCount(); i++ ) {
                row.add( initValue );
            }
            rowMap.put( rowKey, rowPos );
        } else {
            rowPos = rowMap.get( rowKey );
            row = matrix.get( rowPos );
        }
        int columnPos;
        if ( !columnMap.containsKey( columnKey ) ) {
            columnPos = getColumnCount();
            matrix.stream().forEach( list -> list.add( initValue ) );
            columnMap.put( columnKey, columnPos );
        } else {
            columnPos = columnMap.get( columnKey );
        }
        row.set( columnPos, element );
    }

    public E get( int row, int column ) {
        return matrix.get( row ).get( column );
    }

    public E get( K rowKey, K columnKey ) {
        return matrix.get( getRowKeyPosition( rowKey ) ).get( getColumnKeyPosition( columnKey ) );
    }

    private void checkKey( TObjectIntMap<K> keyMap, K key ) {
        if ( !keyMap.containsKey( key ) ) {
            throw new IllegalArgumentException( "Unknown key: " + key );
        }
    }

    /**
     * Fills in empty holes, maps element (rows/columns) according to their
     * columns/rows
     */
    public void revalidate() {
        while ( getRowCount() < getColumnCount() ) {
            int pos = getRowCount();
            List<E> row = new ArrayList<>();
            for ( int i = 0; i < getColumnCount(); i++ ) {
                row.add( initValue );
            }
            matrix.add( row );
            K key = columnMap.keySet().stream().filter( k -> ( columnMap.get( k ) == pos ) ).findAny().orElseThrow( () -> new IllegalStateException( "More columns but no keys available for position: " + pos ) );
            rowMap.put( key, pos );
        }
        while ( getColumnCount() < getRowCount() ) {
            int pos = getColumnCount();
            matrix.stream().forEach( list -> list.add( initValue ) );
            K key = rowMap.keySet().stream().filter( k -> ( rowMap.get( k ) == pos ) ).findAny().orElseThrow( () -> new IllegalStateException( "More rows but no keys available for position: " + pos ) );
            columnMap.put( key, pos );
        }
    }
}
