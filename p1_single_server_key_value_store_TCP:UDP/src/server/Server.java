package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

/**
 * two protocol types
 */
enum ProtocolType {
    TCP, UDP
}

/**
 * a server that supports UDP and TCP
 */
public class Server {
    private static int port;
    private static ServerLogger logger;
    private static KeyValue db;
    private static ServerSocket serverSocket;
    private static DatagramSocket datagramSocket;
    private static ProtocolType protocolType;

    public static void main(String[] args) throws IOException {
        // check and set ip and port
        try {
            initConfig(args);
        } catch (Exception e) {
            logger.printLog("Error: " + e.getMessage());
            return;
        }

        try {
            // Register service on port
            if (protocolType == ProtocolType.TCP) {
                serverSocket = new ServerSocket(port);
            } else {
                datagramSocket = new DatagramSocket(port);
            }
        } catch (BindException e) {
            logger.printLog("Error: " + e.getMessage());
            return;
        }

        logger.printLog(protocolType + " server running on port " + port);

        while (true) {
            if (protocolType == ProtocolType.TCP) {
                TCPHandler();
            } else {
                UDPHandler();
            }
        }
    }

    // handle UDP request
    private static void UDPHandler() throws IOException {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        datagramSocket.receive(packet);

        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();

        String request = new String(packet.getData(), 0, packet.getLength());

        // handle request
        Response response = handleRequest(clientAddress.toString(),
                clientPort, request);

        buf = response.toString().getBytes();

        DatagramPacket resPacket = new DatagramPacket(buf, buf.length, clientAddress, clientPort);
        datagramSocket.send(resPacket);
        logger.printLog(String.format("Response to <%s>:<%d>: %s", clientAddress,
                clientPort, response));
    }

    // handle TCP request
    private static void TCPHandler() throws IOException {
        Socket socket = serverSocket.accept(); // Wait and accept a connection

        // Get a communication stream associated with the socket
        InputStream socketInputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(socketInputStream);

        // receive string from client
        String request = dataInputStream.readUTF();

        // handle request
        Response response = handleRequest(socket.getInetAddress().toString(),
                socket.getPort(), request);

        OutputStream socketOutputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream);

        // Send the request to client
        dataOutputStream.writeUTF(response.toString());
        logger.printLog(String.format("Response to <%s>:<%d>: %s", socket.getInetAddress(),
                socket.getPort(), response));

        // Close the connection, but not the server socket
        dataOutputStream.close();
        socketOutputStream.close();
        socket.close();
    }


    // check and initialize configuration
    private static void initConfig(String[] args) throws IllegalArgumentException {
        logger = new ServerLogger();
        db = new KeyValue();

        if (args.length != 2) {
            throw new IllegalArgumentException("arguments should include [port] and [protocol]");
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

        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "tcp":
                protocolType = ProtocolType.TCP;
                break;
            case "udp":
                protocolType = ProtocolType.UDP;
                break;
            default:
                throw new IllegalArgumentException("Invalid protocol type");
        }
    }

    // handle and log request
    private static Response handleRequest(String address, int port, String req) {
        Response response;
        try {
            checkRequest(req);
            logger.printLog(String.format("Request from <%s>:<%d>: %s ", address, port, req));
            response = getResponse(req);
        } catch (IllegalArgumentException e) {
            response = new Response(ResponseType.ERROR, e.getMessage());
            logger.printLog(String.format("Received malformed request from <%s>:<%d>: %s",
                    address, port, response.message));
        }

        return response;
    }

    /**
     * check if the request is valid
     *
     * @param req request
     * @throws IllegalArgumentException throws if it's not valid
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
                    throw new IllegalArgumentException(
                            "Invalid Command Arguments, " +
                                    "[put] command accepts two arguments: [value] and [key]");
                }
                break;
            case "get":
                if (commandList.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid Command Arguments, " +
                                    "[get] command accepts one argument: [value]");
                }
                break;
            case "delete":
                if (commandList.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid Command Arguments, " +
                                    "[delete] command accepts one argument: [value]");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid Command");
        }
    }

    /**
     * get response for a request
     *
     * @param req request
     * @return response
     */
    private static Response getResponse(String req) {
        String[] commandList = req.split("\\s+");
        String cmd = commandList[0].toLowerCase(Locale.ROOT);
        String key = commandList[1];

        switch (cmd) {
            case "put":
                String value = commandList[2];
                return new Response(ResponseType.SUCCESS, db.put(key, value));
            case "get":
                return new Response(ResponseType.SUCCESS, db.get(key));
            case "delete":
                return new Response(ResponseType.SUCCESS, db.delete(key));
            default:
                return new Response(ResponseType.ERROR, "Invalid Command");
        }
    }
}