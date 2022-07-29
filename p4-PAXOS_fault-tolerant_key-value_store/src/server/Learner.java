package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Leaner to update key-value store.
 */
public interface Learner extends Remote {

    /**
     * accept a proposal id and value.
     * @param id propose id
     * @param value proposed value
     * @throws RemoteException
     */
    void accept(ProposeId id, DatabaseCommand value) throws RemoteException;

    /**
     * get result for a command
     * @param command command
     * @return result
     * @throws RemoteException
     */
    String getResultForCommand(DatabaseCommand command) throws RemoteException;

    /**
     * get value for a key
     * @param key key
     * @return value
     * @throws RemoteException
     */
    String get(String key) throws RemoteException;

}
