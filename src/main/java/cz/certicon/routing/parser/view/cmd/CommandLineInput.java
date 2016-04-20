/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.view.cmd;

import cz.certicon.routing.parser.model.Command;
import cz.certicon.routing.parser.model.ArgumentsParser;
import cz.certicon.routing.parser.view.Input;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class CommandLineInput implements Input {

    @Override
    public List<Command> parseArgs( String... args ) {
        return ArgumentsParser.parse( args );
    }
    
}
