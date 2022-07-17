package server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import util.Logger;
import util.LoggerImpl;


/**
 * a server that supports rmi.
 */
public class ServerImpl implements Server {
    /**
     * server port
     */
    private static int port;
    /**
     * server looger
     */
    private static Logger logger;
    /**
     * key-value store
     */
    private static KeyValueImpl db;
    /**
     * coordinator stub
     */
    private static Coordinator coordinator;
    /**
     * received commit or not
     */
    private static boolean commitReceived;

    /**
     * start a new server and register stub
     *
     * @param args arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            ServerImpl server = new ServerImpl(args);
            Server stub = (Server) UnicastRemoteObject.exportObject(server, 0);

            // bind the stub in the registry
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("KeyValue", stub);

            logger.printLog("Key-Value store server running on port " + port);

            coordinator.addServer(port);
            logger.printLog("Server added to coordinator");
        } catch (Exception e) {
            logger.printLog("Server Error: " + e.getMessage());
        }
    }

    /**
     * init a new server object with ports
     *
     * @param args ports
     * @throws NotBoundException
     * @throws RemoteException
     */
    public ServerImpl(String[] args) throws NotBoundException, RemoteException {
        initConfig(args);
        db = new KeyValueImpl(logger);
    }

    // check and initialize configuration
    private void initConfig(String[] args) throws IllegalArgumentException, RemoteException, NotBoundException {
        logger = new LoggerImpl();

        if (args.length != 2) {
            throw new IllegalArgumentException("arguments should only include [server port] [coordinator port]");
        }
        // check if port is a number
        port = checkPort(args[0]);

        int coordinatorPort = checkPort(args[1]);
        Registry registry = LocateRegistry.getRegistry(coordinatorPort);
        coordinator = (Coordinator) registry.lookup("KeyValue");
    }

    /**
     * check if a port is valid
     *
     * @param portStr string of a port
     * @return port number
     * @throws IllegalArgumentException throws if invalid
     */
    private int checkPort(String portStr) throws IllegalArgumentException {
        int currPort;
        try {
            currPort = Integer.parseInt(portStr);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Port must be a number");
        }
        // check if port is valid
        if (currPort < 0 || currPort > 65535) {
            throw new IllegalArgumentException("Port must between 0 and 65535");
        }
        return currPort;
    }

    /**
     * check if a command should be performed
     *
     * @param key   key
     * @param value value
     * @return true if it should be performed
     * @throws RemoteException
     */
    private boolean checkValidity(String key, String value) throws RemoteException {
        try {
            commitReceived = false;
            coordinator.requestPrepare(key, value);
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            logger.printLog("Error: " + ie);
        }

        return commitReceived;
    }


    @Override
    public String put(String key, String val) throws RemoteException {
        if (checkValidity(key, val)) {
            return "Success";
        } else {
            coordinator.ack(key, val, AckType.ABORT);
            return "Failed";
        }
    }


    @Override
    public String get(String key) throws RemoteException {
        return db.get(key);
    }

    @Override
    public String delete(String key) throws RemoteException {
        if (checkValidity(key, null)) {
            return "Success";
        } else {
            coordinator.ack(key, null, AckType.ABORT);
            return "Failed";
        }
    }

    @Override
    public void prepare(String key, String val) throws RemoteException {
        logger.printLog("PREPARE for " + key + " " + val);

        MessageType messageType = db.canPerform(key, val) ? MessageType.YES : MessageType.NO;
        coordinator.response(key, val, messageType);
    }

    @Override
    public void commit(String key, String val) throws RemoteException {
        logger.printLog("COMMIT for " + key + " " + val);
        if (val != null) {
            db.put(key, val);
        } else {
            db.delete(key);
        }
        coordinator.ack(key, val, AckType.COMMIT);
        commitReceived = true;
    }

    @Override
    public void abort(String key, String val) throws RemoteException {
        logger.printLog("ABORT for " + key + " " + val);
        commitReceived = false;
    }

    @Override
    public int getPort() throws RemoteException {
        return port;
    }
}