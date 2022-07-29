package server;


/**
 * key-value store interface
 */
public interface KeyValue {
    /**
     * put key-value into the store
     *
     * @param key   key
     * @param value value of the key
     * @return last value of the key
     */
    String put(String key, String value);

    /**
     * get value of the key
     *
     * @param key key to get
     * @return value if key exists, null if key doesn't exist
     */
    String get(String key);

    /**
     * delete a key-value pair in the store
     *
     * @param key key to delete
     * @return value of the key
     */
    String delete(String key);

    boolean canPerform(String key, String val);
}
