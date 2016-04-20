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
public class OsmWay {
    // INSERT INTO ways (way_id, timestamp, version, visible, changeset_id)
    // INSERT INTO way_tags (way_id, k, v, version)
    // INSERT INTO way_nodes (way_id, node_id, sequence_id, version)

    private final long id;
    private final Timestamp timestamp;
    private final int version;
    private final boolean visible;
    private final long changeSetId;
    private final List<OsmTag> tags;
    private final List<OsmWayNode> nodes;

    public OsmWay( long id, Timestamp timestamp, int version, boolean visible, long changeSetId, List<OsmTag> tags, List<OsmWayNode> nodes ) {
        this.id = id;
        this.timestamp = timestamp;
        this.version = version;
        this.visible = visible;
        this.changeSetId = changeSetId;
        this.tags = tags;
        this.nodes = nodes;
    }

    public long getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public boolean isVisible() {
        return visible;
    }

    public long getChangeSetId() {
        return changeSetId;
    }

    public List<OsmTag> getTags() {
        return tags;
    }

    public List<OsmWayNode> getNodes() {
        return nodes;
    }
}
