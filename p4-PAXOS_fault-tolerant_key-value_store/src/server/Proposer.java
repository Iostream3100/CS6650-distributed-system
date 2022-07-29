package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * proposer interface to start PAXOS with a value.
 */
public interface Proposer extends Remote {

    /**
     * start a PAXOS
     * @param value value
     * @throws RemoteException
     */
    void PAXOS(DatabaseCommand value) throws RemoteException;
}