/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.commands;

import cz.certicon.routing.parser.controller.ParserController;
import cz.certicon.routing.parser.model.Command;
import cz.certicon.routing.parser.model.DataType;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class DataTypeCommand implements Command {

    private final DataType dataType;

    public DataTypeCommand( DataType dataType ) {
        this.dataType = dataType;
    }

    @Override
    public void execute( ParserController controller ) throws IOException {
        controller.setDataType( dataType );
    }

}
