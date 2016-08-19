/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.sara;

import cz.certicon.routing.parser.model.entity.sra.*;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface DataTarget {

    public void open() throws IOException;

    public void insert( Edge edge ) throws IOException;

    public void insert( Node node ) throws IOException;

    public void insert( NodeToEdge nodeToEdge ) throws IOException;

    public void insert( TurnTable turnRestriction ) throws IOException;

    public void insert( TurnTableValue turnRestrictionArray ) throws IOException;

    public void close() throws IOException;

    public void setExecutionProperties( Properties properties );

}
