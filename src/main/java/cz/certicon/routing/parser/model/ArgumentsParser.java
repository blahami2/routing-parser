/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model;

import cz.certicon.routing.parser.model.commands.SourcePbfFileCommand;
import cz.certicon.routing.parser.model.commands.TargetDbFileCommand;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ArgumentsParser {

    public static List<Command> parse( String... args ) {
        List<Command> commands = new ArrayList<>();
        for ( String arg : args ) {
            if ( arg.startsWith( "parallel=" ) ) {
                commands.add( new SourcePbfFileCommand( arg.substring( "source_pbf_file=".length() ) ) );
            } else if ( arg.startsWith( "" ) ) {
            } else {

            }
        }
        for ( String arg : args ) {
            if ( arg.startsWith( "source_pbf_file=" ) ) {
                commands.add( new SourcePbfFileCommand( arg.substring( "source_pbf_file=".length() ) ) );
            } else if ( arg.startsWith( "target_db_file=" ) ) {
                commands.add( new TargetDbFileCommand( arg.substring( "target_db_file=".length() ) ) );
            } else {

            }
        }
        return commands;
    }

}
