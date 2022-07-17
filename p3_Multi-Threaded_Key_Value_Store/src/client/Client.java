package client;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;
import java.util.Scanner;

import server.KeyValue;
import server.Server;
import util.Logger;
import util.LoggerImpl;

/**
 * client class for user to enter command and call rmi class.
 */
public class Client {
    /**
     * server ip
     */
    private static String ip;

    /**
     * server port
     */
    private static int port;


    /**
     * logger to print and store logs
     */
    private static Logger logger;

    /**
     * pre-populate commands to run when client starts
     */
    private static final String[] prePopulateCommands = new String[]{"put a andrew", "put b boy", "put c car", "put d desk", "put e earth"};

    /**
     * server stub
     */
    private static Server server;

    // java client.Client [ip or hostname] [port] [protocol]
    public static void main(String[] args) throws IOException {
        try {
            initConfig(args);
            Registry registry = LocateRegistry.getRegistry(ip, port);
            server = (Server) registry.lookup("KeyValue");

        } catch (Exception e) {
            logger.printLog("[Error] " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        logger.printLog("client started");

        try {
            prePopulate();
        } catch (Exception e) {
            logger.printLog("[Error] " + e.getMessage());
            return;
        }

        while (true) {
            try {
                logger.printLog("Please enter your command");
                String request = scanner.nextLine();
                if (request.length() > 80) {
                    System.out.println("Maximum 80 characters");
                    continue;
                }

                checkRequest(request);
                String response = getResponse(request);
                logger.printLog(response);
            } catch (Exception e) {
                logger.printLog("[ERROR] " + e.getMessage());
            }
        }
    }

    /**
     * pre populate some requests
     *
     * @throws IOException
     */
    private static void prePopulate() throws IOException {
        for (String request : prePopulateCommands) {
            try {
                logger.printLog("[Pre-populate] " + request);
                checkRequest(request);
                String response = getResponse(request);
                logger.printLog(response);
            } catch (IllegalArgumentException iae) {
                logger.printLog("[ERROR] " + iae.getMessage());
            }
        }
    }


    /**
     * get response for a request
     *
     * @param req request
     * @return string of response
     * @throws RemoteException
     */
    private static String getResponse(String req) throws RemoteException {
        String[] commandList = req.split("\\s+");
        String cmd = commandList[0].toLowerCase(Locale.ROOT);
        String key = commandList[1];
        switch (cmd) {
            case "put":
                String value = commandList[2];
                return String.format("[Success] Response for %s: %s", req, server.put(key, value));
            case "get":
                return String.format("[Success] Response for %s: %s", req, server.get(key));
            case "delete":
                return String.format("[Success] Response for %s: %s", req, server.delete(key));
            default:
                throw new IllegalArgumentException("invalid command");
        }
    }

    /**
     * check if a request is valid
     *
     * @param req request to check
     * @throws IllegalArgumentException throws an exception if the request is invlaid
     */
    private static void checkRequest(String req) throws IllegalArgumentException {
        String[] commandList = req.split("\\s+");
        if (commandList.length < 2) {
            throw new IllegalArgumentException("Invalid Command Arguments");
        }
        String cmd = commandList[0].toLowerCase(Locale.ROOT);

        switch (cmd) {
            case "put":
                if (commandList.length != 3) {
                    throw new IllegalArgumentException("Invalid Command Arguments, " + "[put] command accepts two arguments: [value] and [key]");
                }
                break;
            case "get":
                if (commandList.length != 2) {
                    throw new IllegalArgumentException("Invalid Command Arguments, " + "[get] command accepts one argument: [value]");
                }
                break;
            case "delete":
                if (commandList.length != 2) {
                    throw new IllegalArgumentException("Invalid Command Arguments, " + "[delete] command accepts one argument: [value]");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid Command");
        }
    }


    // check and initialize configuration
    private static void initConfig(String[] args) throws IllegalArgumentException {
        logger = new LoggerImpl();

        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "arguments should include [ip or hostname] and [port] ");
        }

        ip = args[0];
        // check if port is a number
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Port must be a number");
        }
        // check if port is valid
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must between 0 and 65535");
        }
    }
}