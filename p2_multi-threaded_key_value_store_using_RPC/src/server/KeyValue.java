package server;

/**
 * key-value store interface
 */
public interface KeyValue extends java.rmi.Remote {
    /**
     * put key-value into the store
     * @param key key
     * @param value value of the key
     * @return last value of the key
     * @throws java.rmi.RemoteException
     */
    String put(String key, String value) throws java.rmi.RemoteException;

    /**
     * get value of the key
     * @param key key to get
     * @return value if key exists, null if key doesn't exist
     * @throws java.rmi.RemoteException
     */
    String get(String key) throws java.rmi.RemoteException;

    /**
     * delete a key-value pair in the store
     * @param key key to delete
     * @return value of the key
     * @throws java.rmi.RemoteException
     */
    String delete(String key) throws java.rmi.RemoteException;
}
