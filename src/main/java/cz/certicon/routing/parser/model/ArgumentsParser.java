/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model;

import cz.certicon.routing.parser.model.commands.DataTypeCommand;
import cz.certicon.routing.parser.model.commands.PropertyCommand;
import cz.certicon.routing.parser.model.commands.SourcePbfFileCommand;
import cz.certicon.routing.parser.model.commands.TargetDbFileCommand;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ArgumentsParser {

    public static List<Command> parse( String... args ) {
        final List<Command> commands = new ArrayList<>();
        Group<String> settings = new XorGroup<>();
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

        Group<String> files = new XorGroup<>();
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
        for ( String arg : args ) {
            files.execute( arg );
        }
        return commands;
    }

    private static abstract class PropertyCommandChainLink implements ChainLink<String> {

        @Override
        public boolean execute( String t ) {
            String[] split = t.split( "=" );
            onParse( new PropertyCommand( split[0], split[1] ) );
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

    private interface ChainLink<Traveller> {

        public boolean execute( Traveller t );
    }

    private interface Group<Traveller> extends ChainLink<Traveller> {

        public void addChainLink( ChainLink cl );

        public boolean next( Traveller t );
    }

    private static abstract class SimpleGroup<Traveller> implements Group<Traveller> {

        private final List<ChainLink<Traveller>> list = new LinkedList<>();
        private Iterator<ChainLink<Traveller>> iterator = null;

        @Override
        public void addChainLink( ChainLink cl ) {
            list.add( cl );
        }

        @Override
        public boolean execute( Traveller t ) {
            iterator = list.iterator();
            return next( t );
        }

        @Override
        public boolean next( Traveller t ) {
            if ( !getIterator().hasNext() ) {
                return false;
            }
            ChainLink<Traveller> next = getIterator().next();
            return executeNext( next, t );
        }

        abstract protected boolean executeNext( ChainLink<Traveller> next, Traveller t );

        protected Iterator<ChainLink<Traveller>> getIterator() {
            return iterator;
        }
    }

    private static class XorGroup<Traveller> extends SimpleGroup<Traveller> {

        @Override
        protected boolean executeNext( ChainLink<Traveller> next, Traveller t ) {
            if ( !next.execute( t ) ) {
                return next( t );
            } else {
                return true;
            }
        }
    }

    private static class OrGroup<Traveller> extends SimpleGroup<Traveller> {

        @Override
        protected boolean executeNext( ChainLink<Traveller> next, Traveller t ) {
            return next.execute( t );
        }

    }

}
