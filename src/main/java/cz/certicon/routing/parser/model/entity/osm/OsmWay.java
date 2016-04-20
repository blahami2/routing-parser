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
public class OsmWay extends OsmEntity {
    // INSERT INTO ways (way_id, timestamp, version, visible, changeset_id)
    // INSERT INTO way_tags (way_id, k, v, version)
    // INSERT INTO way_nodes (way_id, node_id, sequence_id, version)

    private final List<OsmWayNode> nodes;

    public OsmWay( long id, Timestamp timestamp, int version, boolean visible, long changeSetId, int userId, List<OsmTag> tags, List<OsmWayNode> nodes ) {
        super( id, timestamp, version, visible, changeSetId, userId, tags );
        this.nodes = nodes;
    }

    public List<OsmWayNode> getNodes() {
        return nodes;
    }

}
