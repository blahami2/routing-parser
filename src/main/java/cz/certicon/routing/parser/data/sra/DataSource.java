/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.sra;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface DataSource {
    
    public void read( DataTarget target ) throws IOException;
    
    public void setExecutionProperties(Properties properties);
}
