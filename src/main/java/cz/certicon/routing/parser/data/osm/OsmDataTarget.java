/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.data.osm;

import cz.certicon.routing.parser.model.entity.osm.OsmRelation;
import cz.certicon.routing.parser.model.entity.osm.OsmNode;
import cz.certicon.routing.parser.model.entity.osm.OsmWay;
import java.io.IOException;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface OsmDataTarget {
    public void open() throws IOException;
    public boolean insertWay(OsmWay way) throws IOException;
    public boolean insertNode(OsmNode node) throws IOException;
    public boolean insertRelation(OsmRelation relation) throws IOException;
    public void close() throws IOException;
}
