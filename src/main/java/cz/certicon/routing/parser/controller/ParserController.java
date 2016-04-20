/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.controller;

import cz.certicon.routing.parser.data.OsmDataSource;
import cz.certicon.routing.parser.data.OsmDataTarget;
import cz.certicon.routing.parser.view.Input;
import cz.certicon.routing.parser.view.Output;
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
    private OsmDataTarget osmDataTarget;
    // model

    public ParserController() {
    }

    public void run( String... args ) {
        input.parseArgs( args ).forEach( c -> {
            try {
                c.execute( this );
            } catch ( IOException ex ) {
                Logger.getLogger( ParserController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } );
        try {
            osmDataSource.read( osmDataTarget );
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

    public OsmDataTarget getOsmDataTarget() {
        return osmDataTarget;
    }

    public void setOsmDataTarget( OsmDataTarget osmDataTarget ) {
        this.osmDataTarget = osmDataTarget;
    }

}
