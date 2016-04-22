/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.osm.database;

import cz.certicon.routing.parser.data.osm.OsmDataTarget;
import cz.certicon.routing.parser.data.osm.OsmDataTargetFactory;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class PostgresqlOsmDataTargetFactory implements OsmDataTargetFactory {

    private final Properties properties;
    private boolean initialized = false;

    public PostgresqlOsmDataTargetFactory( Properties properties ) {
        this.properties = properties;
        if ( !initialized ) {
            System.out.println( "initializing" );
            String user = properties.getProperty( "user" );
            String database = properties.getProperty( "database" );
            String password = properties.getProperty( "password" );
            String tablespace = properties.getProperty( "tablespace" );
            String tempTablespace = properties.getProperty( "temp_tablespace" );
            String postgisPath = properties.getProperty( "postgispath" );
            String osmosisPath = properties.getProperty( "osmosispath" );
            try {
                Runtime.getRuntime().exec( "dropdb - U " + user + " -O " + user + " -E utf8 -D " + tablespace + " --lc-collate=\"Czech_Czech Republic.1250\" --lc-ctype=\"Czech_Czech Republic.1250\" " + database );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -c \"ALTER DATABASE %database% SET temp_tablespaces = '" + tempTablespace + "';\" > NUL " );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -f \"" + postgisPath + "\\postgis.sql\" > NUL" );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -f \"" + postgisPath + "\\spatial_ref_sys.sql\" > NUL" );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -c \"CREATE EXTENSION hstore;\" > NUL " );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -f \"" + osmosisPath + "\\script\\pgsnapshot_schema_0.6.sql\" > NUL" );
                Runtime.getRuntime().exec( "psql -U " + user + " -d " + database + " -f \"" + osmosisPath + "\\script\\pgsnapshot_schema_0.6_linestring.sql\" > NUL " );
                initialized = true;
            } catch ( IOException ex ) {
                System.out.println( "exception" );
                Logger.getLogger( PostgresqlOsmDataTargetFactory.class.getName() ).log( Level.SEVERE, null, ex );
            }
            System.out.println( "done initializing" );
        }
    }

    @Override
    public OsmDataTarget createOsmDataTarget() {
        System.out.println( "Created factory" );
        return new PostgresqlOsmDataTarget( properties );
    }

}
