/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.controller;

import cz.certicon.routing.parser.data.OsmDataSource;
import cz.certicon.routing.parser.data.OsmDataTarget;
import cz.certicon.routing.parser.data.OsmDataTargetFactory;
import cz.certicon.routing.parser.view.Input;
import cz.certicon.routing.parser.view.Output;
import cz.certicon.routing.parser.view.cmd.CommandLineInput;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParserController {

    // view
    private Input input;
    private Output output;
    // data
    private OsmDataSource osmDataSource;
    private OsmDataTargetFactory osmDataTargetFactory;
    // model
    // settings
    private boolean parallel = true;

    public ParserController() {
        this.input = new CommandLineInput();
    }

    public void run( String... args ) {
        System.out.println( "Parsing arguments..." );
        input.parseArgs( args ).forEach( c -> {
            try {
                c.execute( this );
            } catch ( IOException ex ) {
                Logger.getLogger( ParserController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
        try {
            System.out.println( "Reading..." );
            if ( parallel ) {
                osmDataSource.read( osmDataTargetFactory );
            } else {
                osmDataSource.read( osmDataTargetFactory.createOsmDataTarget() );
            }
        } catch ( IOException ex ) {
            Logger.getLogger( ParserController.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    public OsmDataSource getOsmDataSource() {
        return osmDataSource;
    }

    public void setOsmDataSource( OsmDataSource osmDataSource ) {
        this.osmDataSource = osmDataSource;
    }

    public OsmDataTargetFactory getOsmDataTargetFactory() {
        return osmDataTargetFactory;
    }

    public void setOsmDataTargetFactory( OsmDataTargetFactory osmDataTargetFactory ) {
        this.osmDataTargetFactory = osmDataTargetFactory;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel( boolean parallel ) {
        this.parallel = parallel;
    }

}
