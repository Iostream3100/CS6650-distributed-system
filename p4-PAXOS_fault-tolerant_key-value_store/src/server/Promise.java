package server;

import java.io.Serializable;

/**
 * promise for a proposed id.
 */
public class Promise implements Serializable {
    public final ProposeId proposeId;
    public final ProposeId acceptedId;
    public final DatabaseCommand acceptedValue;

    /**
     * a promise to an id, but an id and value was accepted before.
     * @param proposeId promised id
     * @param acceptedId accepted id
     * @param acceptedValue accepted value
     */
    public Promise(ProposeId proposeId, ProposeId acceptedId, DatabaseCommand acceptedValue) {
        this.proposeId = proposeId;
        this.acceptedId = acceptedId;
        this.acceptedValue = acceptedValue;
    }

    /**
     * a promise to an id.
     * @param proposeId promised id
     */
    public Promise(ProposeId proposeId) {
        this.proposeId = proposeId;
        this.acceptedValue = null;
        this.acceptedId = null;
    }
}
