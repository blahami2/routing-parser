/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.entity.sra;

import lombok.Value;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class Edge {

    long id;
    long source;
    long target;
    int sourcePosition;
    int targetPosition;
    boolean oneway;
    boolean paid;
    /**
     * Length in meters
     */
    double length;
    /**
     * Speed in kmph
     */
    double speedForward;
    /**
     * Speed in kmph
     */
    double speedBackward;
    String geometry;
}
