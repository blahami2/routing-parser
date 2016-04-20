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
public class OsmRelation extends OsmEntity {
    // INSERT INTO relations (relation_id, timestamp, version, visible, changeset_id)
    // INSERT INTO relation_tags (relation_id, k, v, version)
    // INSERT INTO relation_members (relation_id, member_type, member_id, sequence_id, member_role, version)

    private final List<OsmRelationMember> members;

    public OsmRelation( long id, Timestamp timestamp, int version, boolean visible, long changeSetId, int userId, List<OsmTag> tags, List<OsmRelationMember> members ) {
        super( id, timestamp, version, visible, changeSetId, userId, tags );
        this.members = members;
    }

    public List<OsmRelationMember> getMembers() {
        return members;
    }
}
