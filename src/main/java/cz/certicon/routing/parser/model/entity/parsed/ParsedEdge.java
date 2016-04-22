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
public class ParsedEdge {

    private final long id;
    private final long dataId;
    private final boolean isForward;
    private final long sourceId;
    private final long targetId;

    public ParsedEdge( long id, long dataId, boolean isForward, long sourceId, long targetId ) {
        this.id = id;
        this.dataId = dataId;
        this.isForward = isForward;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public long getId() {
        return id;
    }

    public long getDataId() {
        return dataId;
    }

    public boolean isIsForward() {
        return isForward;
    }

    public long getSourceId() {
        return sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

}
