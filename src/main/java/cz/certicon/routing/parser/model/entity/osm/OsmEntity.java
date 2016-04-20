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
public class OsmEntity {

    private final long id;
    private final Timestamp timestamp;
    private final int version;
    private final boolean visible;
    private final long changeSetId;
    private final int userId;
    private final List<OsmTag> tags;

    public OsmEntity( long id, Timestamp timestamp, int version, boolean visible, long changeSetId, int userId, List<OsmTag> tags ) {
        this.id = id;
        this.timestamp = timestamp;
        this.version = version;
        this.visible = visible;
        this.changeSetId = changeSetId;
        this.tags = tags;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getVersion() {
        return version;
    }

    public boolean isVisible() {
        return visible;
    }

    public long getChangeSetId() {
        return changeSetId;
    }

    public int getUserId() {
        return userId;
    }

    public List<OsmTag> getTags() {
        return tags;
    }

}
