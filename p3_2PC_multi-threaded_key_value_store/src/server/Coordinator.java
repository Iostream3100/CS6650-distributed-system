package server;

import java.rmi.NotBoundException;
import java.rmi.Remote;

/**
 * Coordinator interface to make data consistent among servers
 */
public interface Coordinator extends Remote {
    /**
     * add a server to coordinator
     *
     * @param port server port
     * @throws java.rmi.RemoteException
     * @throws NotBoundException
     */
    void addServer(int port) throws java.rmi.RemoteException, NotBoundException;

    /**
     * prepare request for all servers
     *
     * @param key key of the request
     * @param val value of the request
     * @throws java.rmi.RemoteException
     */
    void requestPrepare(String key, String val) throws java.rmi.RemoteException;

    /**
     * server's response for a prepare
     *
     * @param key  key of the request
     * @param val  value of the request
     * @param type response message type
     * @throws java.rmi.RemoteException
     */
    void response(String key, String val, MessageType type) throws java.rmi.RemoteException;

    /**
     * ack response for a commit or abort
     *
     * @param key     key of the request
     * @param val     value of the request
     * @param ackType ack type
     * @throws java.rmi.RemoteException
     */
    void ack(String key, String val, AckType ackType) throws java.rmi.RemoteException;

}
