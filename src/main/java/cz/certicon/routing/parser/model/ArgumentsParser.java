/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model;

import cz.certicon.routing.parser.model.commands.DataTypeCommand;
import cz.certicon.routing.parser.model.commands.ParsedSourceDbFileCommand;
import cz.certicon.routing.parser.model.commands.ParsedTargetSqliteFileCommand;
import cz.certicon.routing.parser.model.commands.PropertyCommand;
import cz.certicon.routing.parser.model.commands.SourcePbfFileCommand;
import cz.certicon.routing.parser.model.commands.TargetDbFileCommand;
import cz.certicon.routing.utils.cor.ChainGroup;
import cz.certicon.routing.utils.cor.ChainLink;
import cz.certicon.routing.utils.cor.XorChainGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ArgumentsParser {

    public static List<Command> parse( String... args ) {
        final List<Command> commands = new ArrayList<>();
        ChainGroup<String> settings = new XorChainGroup<>();
        settings.addChainLink( new CommandChainLink( "data_type" ) {
            @Override
            public void onSuccess( String value ) {
                commands.add( new DataTypeCommand( DataType.valueOfIgnoreCase( value ) ) );
            }
        } );
        settings.addChainLink( new PropertyCommandChainLink() {
            @Override
            public void onParse( PropertyCommand propertyCommand ) {
                commands.add( propertyCommand );
            }
        } );
        for ( String arg : args ) {
            settings.execute( arg );
        }

        ChainGroup<String> files = new XorChainGroup<>();
        files.addChainLink( new CommandChainLink( "source_pbf_file" ) {
            @Override
            public void onSuccess( String value ) {
                commands.add( new SourcePbfFileCommand( value ) );
            }
        } );
        files.addChainLink( new CommandChainLink( "target_db_file" ) {
            @Override
            public void onSuccess( String value ) {
                commands.add( new TargetDbFileCommand( value ) );
            }
        } );
        files.addChainLink( new CommandChainLink( "source_db_file" ) {
            @Override
            public void onSuccess( String value ) {
                commands.add( new ParsedSourceDbFileCommand( value ) );
            }
        } );
        files.addChainLink( new CommandChainLink( "target_sqlite_file" ) {
            @Override
            public void onSuccess( String value ) {
                commands.add( new ParsedTargetSqliteFileCommand( value ) );
            }
        } );
        for ( String arg : args ) {
            files.execute( arg );
        }
        return commands;
    }

    private static abstract class PropertyCommandChainLink implements ChainLink<String> {

        @Override
        public boolean execute( String t ) {
//            System.out.println( getClass().getSimpleName() + " -> " + t );
            String[] split = t.split( "=" );
            onParse( new PropertyCommand( split[0], split[1] ) );
//            System.out.println( getClass().getSimpleName() + " -> " + Arrays.toString( split ) );
            return false;
        }

        abstract public void onParse( PropertyCommand propertyCommand );
    }

    private static abstract class CommandChainLink implements ChainLink<String> {

        private final String key;

        public CommandChainLink( String key ) {
            this.key = key + "=";
        }

        @Override
        public boolean execute( String t ) {
            if ( t.startsWith( key ) ) {
                onSuccess( t.substring( key.length() ) );
                return true;
            }
            return false;
        }

        abstract public void onSuccess( String value );

    }

}
