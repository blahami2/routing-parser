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
    boolean oneway;
    boolean paid;
    double length;
    double speedForward;
    double speedBackward;
    String geometry;
}
