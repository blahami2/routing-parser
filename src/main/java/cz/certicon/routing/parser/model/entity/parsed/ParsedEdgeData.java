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
public class ParsedEdgeData {

    private final long id;
    private final long osmId;
    private final boolean isPaid;
    private final double length;
    private final int speedForward;
    private final int speedBackward;
    private final String geometry;

    public ParsedEdgeData( long id, long osmId, boolean isPaid, double length, int speedForward, int speedBackward, String geometry ) {
        this.id = id;
        this.osmId = osmId;
        this.isPaid = isPaid;
        this.length = length;
        this.speedForward = speedForward;
        this.speedBackward = speedBackward;
        this.geometry = geometry;
    }

    public long getId() {
        return id;
    }

    public long getOsmId() {
        return osmId;
    }

    public boolean isIsPaid() {
        return isPaid;
    }

    public double getLength() {
        return length;
    }

    public int getSpeedForward() {
        return speedForward;
    }

    public int getSpeedBackward() {
        return speedBackward;
    }

    public String getGeometry() {
        return geometry;
    }

}
