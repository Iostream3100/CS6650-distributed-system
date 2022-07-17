package server;

import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import util.Logger;

/**
 * an implementation of the key-value interface, used in rmi
 */
public class KeyValueImpl implements KeyValue {
    // key-value store map
    private final Map<String, String> store;

    // logger to record logs
    private final Logger logger;

    /**
     * create a new keyValueImpl with logger
     *
     * @param logger logs
     */
    public KeyValueImpl(Logger logger) {
        super();
        this.logger = logger;
        store = new ConcurrentHashMap<>();
    }

    @Override
    public String put(String key, String value) {
        logger.printLog(String.format("<put> %s: %s", key, value));
        return store.put(key, value);
    }

    @Override
    public String get(String key) {
        String value = store.get(key);
        logger.printLog(String.format("<get> %s: %s", key, value));
        return store.get(key);
    }

    @Override
    public String delete(String key) {
        logger.printLog(String.format("<delete> %s", key));
        return store.remove(key);
    }

    @Override
    public boolean canPerform(String key, String val) {
        return val != null || store.containsKey(key);
    }


}
