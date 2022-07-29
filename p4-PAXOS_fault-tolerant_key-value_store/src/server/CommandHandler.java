package server;

import util.Logger;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * handles command for a server.
 */
public class CommandHandler implements Runnable, Serializable {
    private final Server server;
    private final Logger logger;
    private int[] serverPorts;

    /**
     * instantiate a new CommandHandler
     * @param server server
     * @param logger logger
     */
    public CommandHandler(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            logger.printLog("Enter command to add acceptors or listeners");
            String command = scanner.nextLine();
            String[] commandList = command.split(" ");

            try {
                switch (commandList[0]) {
                    case "addall":
                        addOtherServers(commandList);
                        break;
                    case "svcnt":
                        logger.printLog("Total server connected: " + server.getServerList().size());
                        break;
                    case "down":
                    case "up":
                        setAcceptorState(commandList);
                        break;
                    default:
                        logger.printLog("Invalid command");
                }
            } catch (Exception e) {
                logger.printLog("Error: " + e.getMessage());
            }
        }
    }

    /**
     * set an acceptor to be failed or not.
     * @param commandList commands
     * @throws RemoteException
     */
    private void setAcceptorState(String[] commandList) throws RemoteException {
        int phase = Integer.parseInt(commandList[1]);
        boolean isRunning = commandList[0].equals("up");
        server.acceptor_setFailState(phase, isRunning);
    }

    /**
     * connect servers
     * @param commandList server ports
     * @throws RemoteException
     * @throws NotBoundException
     */
    private void addOtherServers(String[] commandList) throws RemoteException, NotBoundException {
        this.serverPorts = new int[commandList.length - 1];
        for (int i = 1; i < commandList.length; i++) {
            serverPorts[i - 1] = Integer.parseInt(commandList[i]);
        }

        for (int i = 0; i < serverPorts.length; ++i) {
            Registry registry1 = LocateRegistry.getRegistry(serverPorts[i]);
            Server server1 = (Server) registry1.lookup("KeyValue");
            for (int j = 0; j < serverPorts.length; ++j) {
                try {
                    Registry registry2 = LocateRegistry.getRegistry(serverPorts[j]);
                    Server server2 = (Server) registry2.lookup("KeyValue");
                    server1.addServer(server2);
                } catch (RemoteException e) {
                    logger.printLog("Error: " + e);
                }
            }
        }
    }
}
