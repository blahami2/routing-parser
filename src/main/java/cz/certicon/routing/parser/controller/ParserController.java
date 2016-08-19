/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.controller;

import cz.certicon.routing.model.basic.TimeUnits;
import cz.certicon.routing.parser.data.osm.OsmDataSource;
import cz.certicon.routing.parser.data.osm.OsmDataTarget;
import cz.certicon.routing.parser.data.osm.OsmDataTargetFactory;
import cz.certicon.routing.parser.data.parsed.ParsedDataSource;
import cz.certicon.routing.parser.data.parsed.ParsedDataTarget;
import cz.certicon.routing.parser.data.sara.DataSource;
import cz.certicon.routing.parser.data.sara.DataTarget;
import cz.certicon.routing.parser.model.DataType;
import cz.certicon.routing.parser.view.Input;
import cz.certicon.routing.parser.view.Output;
import cz.certicon.routing.parser.view.cmd.CommandLineInput;
import cz.certicon.routing.utils.measuring.TimeMeasurement;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParserController {

    private DataType dataType;
    // view
    private Input input;
    private Output output;
    // data
    private OsmDataSource osmDataSource;
    private OsmDataTargetFactory osmDataTargetFactory;
    private DataSource parsedDataSource;
    private DataTarget parsedDataTarget;
    // model
    // settings
    private Map<String, String> properties = new HashMap<>();

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
            System.out.println( "Parsing data..." );
            TimeMeasurement time = new TimeMeasurement();
            time.setTimeUnits( TimeUnits.MILLISECONDS );
            time.start();
            if ( dataType.equals( DataType.OSM ) ) {
                if ( "true".equals( getProperty( "parallel" ) ) ) {
                    osmDataSource.read( osmDataTargetFactory );
                } else {
                    osmDataSource.read( osmDataTargetFactory.createOsmDataTarget() );
                }
            } else {
                Properties executionProperties = new Properties();
                properties.entrySet().stream().forEach( ( entry ) -> {
                    executionProperties.put( entry.getKey(), entry.getValue() );
                } );
                parsedDataSource.setExecutionProperties( executionProperties );
                parsedDataTarget.setExecutionProperties( executionProperties );
                parsedDataSource.read( parsedDataTarget );
            }
            System.out.println( "Parsing done in " + time.stop() + " ms!" );
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

    public void setDataType( DataType dataType ) {
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DataSource getParsedDataSource() {
        return parsedDataSource;
    }

    public void setParsedDataSource( DataSource parsedDataSource ) {
        this.parsedDataSource = parsedDataSource;
    }

    public DataTarget getParsedDataTarget() {
        return parsedDataTarget;
    }

    public void setParsedDataTarget( DataTarget parsedDataTarget ) {
        this.parsedDataTarget = parsedDataTarget;
    }

    public void setProperty( String key, String value ) {
        properties.put( key, value );
    }

    public String getProperty( String key ) {
        return properties.get( key );
    }
}
