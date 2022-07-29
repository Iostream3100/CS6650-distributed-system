package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * a server interface that communicates with coordinator and client
 */
public interface Server extends Remote {
    /**
     * put a key-value pair
     *
     * @param key   key
     * @param value value
     * @return Success or Failed
     * @throws java.rmi.RemoteException
     */
    String put(String key, String value) throws java.rmi.RemoteException;

    /**
     * get the value of a key
     *
     * @param key key
     * @return value of the key
     * @throws java.rmi.RemoteException
     */
    String get(String key) throws java.rmi.RemoteException;

    /**
     * delete a key
     *
     * @param key key to delete
     * @return Success of Failed
     * @throws java.rmi.RemoteException
     */
    String delete(String key) throws java.rmi.RemoteException;

    /**
     * get the unique server id
     * @return server id
     * @throws RemoteException
     */
    int getServerId() throws RemoteException;

    /**
     * add a server to the list
     * @param newServer server to add
     * @throws RemoteException
     */
    void addServer(Server newServer) throws RemoteException;

    /**
     * get all the servers
     * @return server list
     * @throws RemoteException
     */
    List<Server> getServerList() throws  RemoteException ;

    /**
     * phase 1, get the promise for a proposed id
     * @param proposeId proposed id
     * @return promise
     * @throws RemoteException
     */
    Promise acceptor_getPreparePromise(ProposeId proposeId) throws RemoteException;

    /**
     * phase 2, proposer propose a value
     * @param proposeId proposed id
     * @param value proposed value
     * @return reponse
     * @throws RemoteException
     */
    Response acceptor_propose(ProposeId proposeId, DatabaseCommand value) throws RemoteException;

    /**
     * set phase 1 or phase 2 to fail or not
     * @param phase phase 1 or 2
     * @param isRunning state
     * @throws RemoteException
     */
    void acceptor_setFailState(int phase, boolean isRunning) throws RemoteException;

    /**
     * clear the state of an acceptor
     * @throws RemoteException
     */
    void acceptor_clearState() throws  RemoteException;

    /**
     * let the learner accept a value
     * @param id proposed id
     * @param value proposed value
     * @throws RemoteException
     */
    void learner_accept(ProposeId id, DatabaseCommand value) throws RemoteException;
}
