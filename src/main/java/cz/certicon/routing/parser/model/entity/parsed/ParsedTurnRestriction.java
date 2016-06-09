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
public class ParsedTurnRestriction {
    private final long fromId;
    private final long viaId;
    private final long toId;

    public ParsedTurnRestriction( long fromId, long viaId, long toId ) {
        this.fromId = fromId;
        this.viaId = viaId;
        this.toId = toId;
    }

    public long getFromId() {
        return fromId;
    }

    public long getViaId() {
        return viaId;
    }

    public long getToId() {
        return toId;
    }
    
    
}
