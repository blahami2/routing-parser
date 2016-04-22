/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.entity.parsed;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class ParsedNodeData {

    private final long id;
    private final long osmId;
    private final String geom;

    public ParsedNodeData( long id, long osmId, String geom ) {
        this.id = id;
        this.osmId = osmId;
        this.geom = geom;
    }

    public long getId() {
        return id;
    }

    public long getOsmId() {
        return osmId;
    }

    public String getGeom() {
        return geom;
    }

}
