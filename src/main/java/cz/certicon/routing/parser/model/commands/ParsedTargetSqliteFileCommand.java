/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.commands;

import cz.certicon.routing.parser.controller.ParserController;
import cz.certicon.routing.parser.data.parsed.ParsedDataTarget;
import cz.certicon.routing.parser.data.parsed.sqlite.SqliteParsedDataTarget;
import cz.certicon.routing.parser.model.Command;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParsedTargetSqliteFileCommand implements Command {

    private final String filename;

    public ParsedTargetSqliteFileCommand( String filename ) {
        this.filename = filename;
    }

    @Override
    public void execute( ParserController controller ) throws IOException {
        File file = new File( filename );
        Properties properties = new Properties();
        properties.load( new FileInputStream( file ) );
        ParsedDataTarget parsedDataTarget = new SqliteParsedDataTarget( properties );
        controller.setParsedDataTarget( parsedDataTarget );
    }

}
