package server;

import java.rmi.Remote;

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
     * prepare to perform a command
     *
     * @param key key of the command
     * @param val value of the command
     * @throws java.rmi.RemoteException
     */
    void prepare(String key, String val) throws java.rmi.RemoteException;

    /**
     * commit a command
     *
     * @param key key
     * @param val value
     * @throws java.rmi.RemoteException
     */
    void commit(String key, String val) throws java.rmi.RemoteException;

    /**
     * abort a command
     *
     * @param key key
     * @param val value
     * @throws java.rmi.RemoteException
     */
    void abort(String key, String val) throws java.rmi.RemoteException;

    /**
     * get the port of current server
     *
     * @return port number
     * @throws java.rmi.RemoteException
     */
    int getPort() throws java.rmi.RemoteException;

}
