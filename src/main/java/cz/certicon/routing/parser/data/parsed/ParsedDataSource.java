/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.parsed;

import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface ParsedDataSource {

    public boolean read( ParsedDataTarget target ) throws IOException;
    
}
