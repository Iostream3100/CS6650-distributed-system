package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

import util.Logger;

/**
 * proposer class.
 */
public class ProposerImpl implements Proposer, Serializable {
    private final Logger logger;
    private final int serverId;
    private int proposeNumber;
    private int promiseReceived;
    DatabaseCommand acceptedValue;
    private ProposeId proposeId;
    private int acceptReceived;
    private boolean consensusReached;
    private final List<Server> serverList;

    public ProposerImpl(Logger logger, int serverId, List<Server> serverList) {
        this.logger = logger;
        this.serverId = serverId;
        this.serverList = serverList;

        this.proposeNumber = 0;
        this.promiseReceived = 0;
        this.acceptedValue = null;
        this.acceptReceived = 0;
        this.consensusReached = false;
    }

    @Override
    public void PAXOS(DatabaseCommand value) throws RemoteException {
        this.promiseReceived = 0;
        this.proposeId = null;
        this.acceptedValue = null;
        this.acceptReceived = 0;
        this.consensusReached = false;

        // Phase 1
        sendProposeToAllAcceptors();

        // Phase 2
        checkResponseAndProposeValue(value);

        // if consensus is reached, update all listeners
        if (consensusReached) {
            updateAllListeners(value);
            resetAllAcceptor();
        }
    }

    /**
     * phase 1, send propose to all Acceptors.
     * @throws RemoteException
     */
    private void sendProposeToAllAcceptors() throws RemoteException {
        this.proposeId = new ProposeId(proposeNumber++, serverId);
        logger.printLog(String.format("[Proposer %d] Start to send proposeId %s to all acceptors", serverId, proposeId));
        for (Server server : serverList) {
            Promise promise = server.acceptor_getPreparePromise(proposeId);
            if (promise != null) {
                promiseReceived++;
                if (promise.acceptedValue != null) {
                    this.acceptedValue = promise.acceptedValue;
                }
            }
        }
    }

    /**
     * phase 2, propose value to all acceptors
     * @param proposeValue value to propose
     * @throws RemoteException
     */
    private void checkResponseAndProposeValue(DatabaseCommand proposeValue) throws RemoteException {
        if (promiseReceived >= getMajority()) {
            DatabaseCommand value = acceptedValue != null ? acceptedValue : proposeValue;

            logger.printLog(String.format("[Proposer %d] Received promise responses from a majority of acceptors (%d/%d)"
                    , serverId, promiseReceived, serverList.size()));

            if (acceptedValue != null) {
                logger.printLog(String.format("[Proposer %d] But a value %s was received in phase 1, starting to propose this value instead"
                        , serverId, acceptedValue));
            } else {
                logger.printLog(String.format("[Proposer %d]  starting to propose %s", serverId, value));
            }

            // send propose to all acceptors
            for (Server server : serverList) {
                Response response = server.acceptor_propose(proposeId, value);
                if (response != null && response.type == ResponseType.ACCEPTED) {
                    acceptReceived++;
                }

                if (acceptReceived >= getMajority()) {
                    logger.printLog(String.format("[Proposer %d] consensus reached on value %s:", serverId, value));
                    consensusReached = true;
                    return;
                }
            }
        } else {
            logger.printLog(String.format("[Proposer %d] Failed to receive promises from majority of acceptors", serverId));
        }
    }

    /**
     * update the value to all listeners
     * @param proposeValue value to propose
     * @throws RemoteException
     */
    private void updateAllListeners(DatabaseCommand proposeValue) throws RemoteException {
        DatabaseCommand value = acceptedValue != null ? acceptedValue : proposeValue;

        for (Server server : serverList) {
            server.learner_accept(proposeId, value);
        }
    }

    /**
     * reset all acceptors after a PAXOS
     * @throws RemoteException
     */
    private void resetAllAcceptor() throws RemoteException {
        for (Server server : serverList) {
            server.acceptor_clearState();
        }
    }

    /**
     * get the number of the majority of acceptors
     * @return
     */
    private int getMajority() {
        return (serverList.size() + 2) / 2;
    }
}
