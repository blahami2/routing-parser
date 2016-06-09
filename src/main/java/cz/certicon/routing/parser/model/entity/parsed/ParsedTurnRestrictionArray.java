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
public class ParsedTurnRestrictionArray {
    private final long arrayId;
    private final int position;
    private final long edgeId;

    public ParsedTurnRestrictionArray( long arrayId, int position, long edgeId ) {
        this.arrayId = arrayId;
        this.position = position;
        this.edgeId = edgeId;
    }

    public long getArrayId() {
        return arrayId;
    }

    public int getPosition() {
        return position;
    }

    public long getEdgeId() {
        return edgeId;
    }
    
    
}
