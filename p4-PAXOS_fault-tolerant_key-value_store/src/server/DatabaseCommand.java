package server;

import java.io.Serializable;

public class DatabaseCommand implements Serializable {
    public final DatabaseCommandType type;
    public final String key;
    public final String value;

    public DatabaseCommand(DatabaseCommandType type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%s %s %s]", type.name(), key, value);
    }

    @Override
    public boolean equals(Object obj) {
        DatabaseCommand other = (DatabaseCommand) obj;
        return key.equals(other.key) && (value == null && other.value == null || value.equals(other.value));
    }
}
