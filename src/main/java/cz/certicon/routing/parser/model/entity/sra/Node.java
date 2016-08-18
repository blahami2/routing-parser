/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.parser.model.entity.sra;

import lombok.Value;
import lombok.experimental.Wither;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
@Value
public class Node {
    long id;
    @Wither
    int turnTableId;
    String geometry;
}
