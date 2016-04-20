/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.commands;

import cz.certicon.routing.parser.controller.ParserController;
import cz.certicon.routing.parser.model.Command;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParallelCommand implements Command {

    private final String value;

    public ParallelCommand( String value ) {
        this.value = value;
    }

    @Override
    public void execute( ParserController controller ) throws IOException {
        controller.setParallel( !value.equals( "false" ) );
    }

}
