package server;

// two types of response
enum ResponseType {
    SUCCESS, ERROR;
}

// response to client
public class Response {
    public final ResponseType type;
    public final String message;

    Response(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + String.valueOf(message);
    }
}
