package server;

import java.io.IOException;
import java.rmi.Naming;

import util.Logger;
import util.LoggerImpl;


/**
 * a server that supports rmi.
 */
public class Server {
    private static int port;
    private static Logger logger;
    private static KeyValueImpl db;

    public static void main(String[] args) throws IOException {
        // check and set port
        try {
            initConfig(args);
        } catch (Exception e) {
            logger.printLog("Error: " + e.getMessage());
            return;
        }

        try {
            db = new KeyValueImpl(logger);
            Naming.rebind("rmi://localhost:" + port + "/keyValue", db);
            logger.printLog("Key-Value store server running on port 1099");
        } catch (Exception e) {
            logger.printLog("Error: " + e.getMessage());
        }
    }

    // check and initialize configuration
    private static void initConfig(String[] args) throws IllegalArgumentException {
        logger = new LoggerImpl();

        if (args.length != 1) {
            throw new IllegalArgumentException("arguments should include [port]");
        }
        // check if port is a number
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Port must be a number");
        }
        // check if port is valid
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must between 0 and 65535");
        }
    }
}