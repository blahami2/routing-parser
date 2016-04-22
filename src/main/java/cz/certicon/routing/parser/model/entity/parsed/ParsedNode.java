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
public class ParsedNode {
    private final long id;
    private final long dataId;

    public ParsedNode( long id, long dataId ) {
        this.id = id;
        this.dataId = dataId;
    }

    public long getId() {
        return id;
    }

    public long getDataId() {
        return dataId;
    }
}
