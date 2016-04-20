/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.commands;

import cz.certicon.routing.data.basic.FileSource;
import cz.certicon.routing.parser.model.Command;
import cz.certicon.routing.parser.controller.ParserController;
import cz.certicon.routing.parser.data.OsmDataSource;
import cz.certicon.routing.parser.data.pbf.OsmPbfDataSource;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SourcePbfFileCommand implements Command {

    private final String filename;

    public SourcePbfFileCommand( String filename ) {
        this.filename = filename;
    }

    @Override
    public void execute( ParserController controller ) throws IOException {
        File file = new File( filename );
        OsmDataSource osmDataSource = new OsmPbfDataSource( new FileSource( file ) );
        controller.setOsmDataSource( osmDataSource );
    }

}
