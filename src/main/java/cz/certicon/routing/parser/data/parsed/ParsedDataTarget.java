/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed;

import cz.certicon.routing.parser.model.entity.parsed.*;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface ParsedDataTarget {

    public void open() throws IOException;

    public void insert( ParsedEdgeData edgeData ) throws IOException;

    public void insert( ParsedEdge edge ) throws IOException;

    public void insert( ParsedNodeData nodeData ) throws IOException;

    public void insert( ParsedNode node ) throws IOException;

    public void close() throws IOException;
    
    public void setExecutionProperties(Properties properties);
}
