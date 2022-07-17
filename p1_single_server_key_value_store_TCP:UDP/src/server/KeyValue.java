package server;

import java.util.HashMap;
import java.util.Map;

// key-value store
public class KeyValue {
    private final Map<String, String> map;

    public KeyValue() {
        map = new HashMap<>();
    }

    public String put(String key, String value) {
        return map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public String delete(String key) {
        return map.remove(key);
    }
}
