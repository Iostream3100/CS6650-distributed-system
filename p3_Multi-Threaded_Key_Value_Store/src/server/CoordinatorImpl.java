package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import util.Logger;
import util.LoggerImpl;


/**
 * an implementation of coordinator interface
 */
public class CoordinatorImpl implements Coordinator {
    /**
     * all the servers in current coordinator
     */
    private static List<Server> serverList;
    /**
     * coordinator port
     */
    private static int port;
    /**
     * logger
     */
    private static Logger logger;
    /**
     * total number of ack received
     */
    private static int ackSum;

    /**
     * registers a coordinator at port
     * @param args port
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        try {
            CoordinatorImpl coordinator = new CoordinatorImpl(args);
            Coordinator stub = (Coordinator) UnicastRemoteObject.exportObject(coordinator, 0);

            // bind the stub in the registry
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("KeyValue", stub);
            logger.printLog("Coordinator Running on port " + port);
        } catch (Exception e) {
            logger.printLog("Server Error:" + e.getMessage());
        }

    }

    /**
     * initialize a new coordinator object
     * @param args port
     */
    public CoordinatorImpl(String[] args) {
        logger = new LoggerImpl();
        serverList = new ArrayList<>();
        initConfig(args);
    }


    @Override
    public void addServer(int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(port);
        Server server = (Server) registry.lookup("KeyValue");
        serverList.add(server);
        logger.printLog(String.format("Server with port [%s] added.", server.getPort()));
    }

    @Override
    public void requestPrepare(String key, String val) throws RemoteException {
        try {
            // set total ack number to 0
            ackSum = 0;
            logger.printLog("REQ_PREPARE for " + key + " " + val);

            for (Server server : serverList) {
                server.prepare(key, val);
            }

            Thread.sleep(300);

            if (ackSum == serverList.size()) {
                for (Server server : serverList) {
                    server.commit(key, val);
                }
            } else {
                for (Server server : serverList) {
                    server.abort(key, val);
                }
            }
        } catch (InterruptedException ie) {
            logger.printLog("Error: " + ie);
        }
    }

    @Override
    public void response(String key, String val, MessageType type) throws RemoteException {
        logger.printLog("RESPONSE for " + key + " " + val + " " +
                (type == MessageType.YES ? "YES" : "NO"));
        if (type == MessageType.YES) {
            ackSum++;
        }
    }

    @Override
    public void ack(String key, String val, AckType ackType) throws RemoteException {
        logger.printLog(String.format("ACK %s for %s %s", ackType, key, val));
    }

    /**
     * check if args and ports are valid
     * @param args port
     * @throws IllegalArgumentException
     */
    private void initConfig(String[] args) throws IllegalArgumentException {
        if (args.length != 1) {
            throw new IllegalArgumentException("arguments should only include [port]");
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