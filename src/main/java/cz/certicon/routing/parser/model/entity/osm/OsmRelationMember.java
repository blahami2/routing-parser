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
public class OsmRelationMember {
    /*    relation_id bigint NOT NULL,
    member_id bigint NOT NULL,
    member_type character(1) NOT NULL,
    member_role text NOT NULL,
    sequence_id int NOT NULL*/
    
    private final long id;
    private final Type type;
    private final String role;
    private final int sequenceId;

    public OsmRelationMember( long id, Type type, String role, int sequenceId ) {
        this.id = id;
        this.type = type;
        this.role = role;
        this.sequenceId = sequenceId;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getRole() {
        return role;
    }

    public int getSequenceId() {
        return sequenceId;
    }
    
    public static enum Type {
        NODE, WAY, RELATION;
    }
}
