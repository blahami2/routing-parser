/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser;

import cz.certicon.routing.parser.controller.ParserController;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParserApplication {

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        ParserController controller = new ParserController();
        controller.run( args );
        new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r ) {
                throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        for ( String arg : args ) {
//            System.out.println( "'" + arg + "'" );
//        }
    }

}
