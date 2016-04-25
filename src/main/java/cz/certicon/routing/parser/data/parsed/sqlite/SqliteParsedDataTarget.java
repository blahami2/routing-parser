/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed.sqlite;

import cz.certicon.routing.parser.data.parsed.ParsedDataTarget;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdge;
import cz.certicon.routing.parser.model.entity.parsed.ParsedEdgeData;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNode;
import cz.certicon.routing.parser.model.entity.parsed.ParsedNodeData;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SqliteParsedDataTarget implements ParsedDataTarget {

    @Override
    public void open() throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insert( ParsedEdgeData edgeData ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insert( ParsedEdge edge ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insert( ParsedNodeData nodeData ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean insert( ParsedNode node ) throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

}
