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
public class OsmWayNode {

    /*    way_id bigint NOT NULL,
    node_id bigint NOT NULL,
    sequence_id int NOT NULL*/
    private final long wayId;
    private final long nodeId;
    private final int sequenceId;

    public OsmWayNode( long wayId, long nodeId, int sequenceId ) {
        this.wayId = wayId;
        this.nodeId = nodeId;
        this.sequenceId = sequenceId;
    }

    public long getWayId() {
        return wayId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public int getSequenceId() {
        return sequenceId;
    }
}
