package server;

import util.Logger;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * An implementation of Acceptor interface.
 */
public class AcceptorImpl implements Acceptor, Serializable {
    // max propose id received.
    private ProposeId maxId;

    // accepted a proposal before or not
    private boolean proposalAccepted;

    // accepted propose id
    private ProposeId acceptedId;

    // accepted value
    private DatabaseCommand acceptedValue;

    // logger
    private final Logger logger;

    // server id
    private final int serverId;

    // phase 1 fail or not
    private boolean phase1Running;

    // phase 2 fail or not
    private boolean phase2Running;

    public AcceptorImpl(Logger logger, int serverId) {
        this.logger = logger;
        this.maxId = new ProposeId(-1, -1);
        this.proposalAccepted = false;
        this.serverId = serverId;
        this.phase1Running = true;
        this.phase2Running = true;
    }

    /**
     * @param proposeId
     * @return
     * @throws RemoteException
     */
    @Override
    public Promise getPreparePromise(ProposeId proposeId)  {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!phase1Running) {
            return null;
        }

        logger.printLog(String.format("[Acceptor %d] received prepare propose ID: %s", serverId, proposeId));
        if (maxId.compareTo(proposeId) > 0) {
            logger.printLog(String.format("[Acceptor %d] rejected propose ID: %s", serverId, proposeId));
            return null;
        } else {
            maxId = proposeId;
            if (proposalAccepted) {
                logger.printLog(String.format("[Acceptor %d] promised propose ID: %s, " +
                        "but propose %s with value %s already accepted", serverId, proposeId, acceptedId, acceptedValue));
                return new Promise(proposeId, acceptedId, acceptedValue);
            } else {
                logger.printLog(String.format("[Acceptor %d] promised propose ID: %s", serverId, proposeId));
                return new Promise(acceptedId);
            }
        }
    }

    @Override
    public Response propose(ProposeId id, DatabaseCommand value)  {
        logger.printLog(String.format("[Acceptor %d] received proposal ID: %s, value: %s",serverId, id, value));

        if (!phase2Running) {
            return null;
        }

        Response response;

        if (id.equals(maxId)) {
            proposalAccepted = true;
            acceptedId = id;
            acceptedValue = value;
            response = new Response(ResponseType.ACCEPTED, id, value);
        } else {
            response = new Response(ResponseType.FAILED, null, null);
        }

        logger.printLog(String.format("[Acceptor %d] responded %s", serverId, response));
        return response;
    }


    @Override
    public void clearState() {
        this.maxId = new ProposeId(-1, -1);
        this.proposalAccepted = false;
    }

    @Override
    public void setFailState(int phase, boolean isRunning) {
        if (phase == 1) {
            phase1Running = isRunning;
        } else if (phase == 2) {
            phase2Running = isRunning;
        }
    }
}
