/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public enum DataType {
    OSM, PARSED;

    public static DataType valueOfIgnoreCase( String value ) {
        for ( DataType value1 : values() ) {
            if ( value1.name().equalsIgnoreCase( value ) ) {
                return value1;
            }
        }
        return valueOf( value );
    }
}
