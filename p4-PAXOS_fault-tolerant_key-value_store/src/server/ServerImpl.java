package server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

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
     * server logger
     */
    private static Logger logger;
    /**
     * key-value store
     */
    private static KeyValueImpl db;

    /**
     * command handler
     */
    private static CommandHandler commandHandler;

    /**
     * server id
     */
    private static int serverId;

    /**
     * proposer
     */
    private static Proposer proposer;

    /**
     * accptor
     */
    private static Acceptor acceptor;

    /**
     * learner
     */
    private static Learner learner;

    /**
     * all servers
     */
    private static List<Server> serverList;


    /**
     * start a new server and register stub
     *
     * @param args arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            ServerImpl server = new ServerImpl(args);

            Server stub = (Server) UnicastRemoteObject.exportObject(server, port);

            // bind the stub in the registry
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("KeyValue", stub);


            logger.printLog("Key-Value store server running on port " + port);

            commandHandler = new CommandHandler(server, logger);
            commandHandler.run();
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
    public ServerImpl(String[] args) throws  RemoteException {
        logger = new LoggerImpl();
        serverList = new ArrayList<>();

        if (args.length != 2) {
            throw new IllegalArgumentException("arguments should only include [server port] [unique server id]");
        }
        // check if port is a number
        port = checkPort(args[0]);

        serverId = Integer.parseInt(args[1]);

        db = new KeyValueImpl(logger);

        proposer = new ProposerImpl(logger, serverId, serverList);
        acceptor = new AcceptorImpl(logger, serverId);
        learner = new LearnerImpl(db, logger, serverId);
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


    @Override
    public String put(String key, String val) throws RemoteException {
        DatabaseCommand command = new DatabaseCommand(DatabaseCommandType.PUT, key, val);
        proposer.PAXOS(command);

        return learner.getResultForCommand(command);
    }

    @Override
    public String get(String key) throws RemoteException {
        return learner.get(key);
    }

    @Override
    public String delete(String key) throws RemoteException {
        DatabaseCommand command = new DatabaseCommand(DatabaseCommandType.DELETE, key, null);
        proposer.PAXOS(command);

        return learner.getResultForCommand(command);
    }

    @Override
    public int getServerId() throws RemoteException {
        return serverId;
    }


    @Override
    public void addServer(Server newServer) throws RemoteException {
        serverList.add(newServer);
        logger.printLog(String.format("server <%d> added to server <%d>", newServer.getServerId(), serverId));
    }

    @Override
    public List<Server> getServerList() throws RemoteException {
        return serverList;
    }

    @Override
    public Promise acceptor_getPreparePromise(ProposeId proposeId) throws RemoteException {
        return acceptor.getPreparePromise(proposeId);
    }

    @Override
    public Response acceptor_propose(ProposeId proposeId, DatabaseCommand value) throws RemoteException {
        return acceptor.propose(proposeId, value);
    }

    @Override
    public void acceptor_setFailState(int phase, boolean isRunning) {
        logger.printLog(String.format("[Acceptor %d] Phase <%d> is set to <%s>", serverId, phase,
                isRunning ? "Normal" : "Fail"));
        acceptor.setFailState(phase, isRunning);
    }

    @Override
    public void learner_accept(ProposeId id, DatabaseCommand value) throws RemoteException {
        learner.accept(id, value);
    }


    @Override
    public void acceptor_clearState() throws RemoteException {
        acceptor.clearState();
    }
}