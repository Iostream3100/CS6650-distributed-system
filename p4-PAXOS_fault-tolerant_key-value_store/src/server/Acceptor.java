package server;


/**
 * Acceptor to promise on an id or accept a value.
 */
public interface Acceptor {
    /**
     * phase 1, try to promise on an id
     * @param proposeId id to promise
     * @return promise
     */
    Promise getPreparePromise(ProposeId proposeId);

    /**
     * phase 2, proposer propose a value
     * @param proposeId proposed id
     * @param value proposed value
     * @return response
     */
    Response propose(ProposeId proposeId, DatabaseCommand value);


    /**
     *  clear the state to receive a new proposal
     */
    void clearState();

    /**
     * set phase 1 or phase 2 to fail or not
     * @param phase phase 1 or 2
     * @param isRunning state
     */
    void setFailState(int phase, boolean isRunning);
}
