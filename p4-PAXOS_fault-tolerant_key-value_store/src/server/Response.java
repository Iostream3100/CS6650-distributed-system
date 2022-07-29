package server;

import java.io.Serializable;

/**
 * response to a proposed id and value.
 */
public class Response implements Serializable {
    public final ResponseType type;
    public final ProposeId id;
    public final DatabaseCommand value;

    /**
     * response
     * @param type response type
     * @param id proposed id
     * @param value proposed value
     */
    public Response(ResponseType type, ProposeId id, DatabaseCommand value) {
        this.type = type;
        this.id = id;
        this.value = value;
    }


    @Override
    public String toString() {
        return String.format("[%s %s %s]", type.name(), id, value);
    }
}
