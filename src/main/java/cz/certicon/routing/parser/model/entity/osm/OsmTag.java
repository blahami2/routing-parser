/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.entity.osm;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class OsmTag {
    // INSERT INTO way_tags (way_id, k, v, version)

    private final long id;
    private final String key;
    private final String value;
    private final int version;

    public OsmTag( long id, String key, String value, int version ) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getVersion() {
        return version;
    }

}
