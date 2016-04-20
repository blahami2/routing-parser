/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.entity.osm;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class OsmNode extends OsmEntity {
    // INSERT INTO nodes(node_id, timestamp, version, visible, changeset_id, latitude, longitude, tile)
    // INSERT INTO node_tags (node_id, k, v, version)

    private final double latitude;
    private final double longitude;
    private final long tile;

    public OsmNode( long id, Timestamp timestamp, int version, boolean visible, long changeSetId, int userId, List<OsmTag> tags, double latitude, double longitude, long tile ) {
        super( id, timestamp, version, visible, changeSetId, userId, tags );
        this.latitude = latitude;
        this.longitude = longitude;
        this.tile = tile;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTile() {
        return tile;
    }

}
