package server;

import util.Logger;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * An implementation of the Learner interface
 */
public class LearnerImpl implements Learner, Serializable {
    private final KeyValue keyValue;
    public final Logger logger;
    private DatabaseCommand acceptedCommand;
    private final int serverId;

    /**
     * instantiate a new Learner
     * @param keyValue key-val store
     * @param logger logger
     * @param serverId server id
     */
    public LearnerImpl(KeyValue keyValue, Logger logger, int serverId) {
        this.keyValue = keyValue;
        this.logger = logger;
        this.acceptedCommand = null;
        this.serverId = serverId;
    }

    @Override
    public void accept(ProposeId proposeId, DatabaseCommand value) throws RemoteException {
        logger.printLog(String.format("[Learner %d] accepted proposal id: %s, value: %s", serverId, proposeId, value));
        acceptedCommand = new DatabaseCommand(value.type, value.key, value.value);
        switch (value.type) {
            case PUT:
                keyValue.put(value.key, value.value);
                break;
            case DELETE:
                keyValue.delete(value.key);
                break;
        }
    }

    @Override
    public String getResultForCommand(DatabaseCommand command) throws RemoteException {
        try {
            Thread.sleep(3100);
        } catch (InterruptedException e) {
            logger.printLog("Error: " + e.getMessage());
        }

        return command.equals(acceptedCommand) ? "Success" : "Failed";
    }


    @Override
    public String get(String key) throws RemoteException {
        logger.printLog(String.format("[Learner %d] %s : %s", serverId, key, keyValue.get(key)));
        return keyValue.get(key);
    }
}
