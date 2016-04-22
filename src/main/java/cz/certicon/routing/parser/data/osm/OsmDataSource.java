/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.osm;

import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface OsmDataSource {

    public boolean read( OsmDataTarget target ) throws IOException;

    public boolean read( OsmDataTargetFactory targetFactory ) throws IOException;
}
