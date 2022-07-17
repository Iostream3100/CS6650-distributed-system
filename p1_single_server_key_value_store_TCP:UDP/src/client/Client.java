package client;// SimpleClient.java: A simple client program.

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

enum ProtocolType {
    TCP, UDP
}

public class Client {
    private static String ip;
    private static int port;
    private static ClientLogger logger;
    private static ProtocolType protocolType;
    private static final String[] prePopulateCommands =
            new String[]{"put a andrew", "put b boy", "put c car", "put d desk", "put e earth"};

    // java client.Client [ip or hostname] [port] [protocol]
    public static void main(String[] args) throws IOException {
        try {
            initConfig(args);
        } catch (Exception e) {
            logger.printLog("[Error] " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        logger.printLog(protocolType + " client started");

        try {
            prePopulate();
        } catch (UnknownHostException e) {
            logger.printLog("[Error] Hostname or ip is invalid: " + e.getMessage());
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

                String response = protocolType == ProtocolType.TCP ?
                        sendRequestTCP(request) : sendRequestUDP(request);
                logger.printLog(response);
            } catch (IOException ioe) {
                logger.printLog("ERROR: " + ioe.getMessage());
            }
        }
    }

    // pre populate some commands
    private static void prePopulate() throws IOException {
        for (String request : prePopulateCommands) {
            logger.printLog("[Pre-populate] " + request);
            String response = protocolType == ProtocolType.TCP ?
                    sendRequestTCP(request) : sendRequestUDP(request);
            logger.printLog(response);
        }
    }

    // send request using TCP
    private static String sendRequestTCP(String request) throws IOException {
        Socket socket = new Socket(ip, port);
        // set a timeout for unresponsive server
        socket.setSoTimeout(2000);

        // Get an input file handle from the socket and read the input
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(request);

        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        String response = dataInputStream.readUTF();

        // When done, just close the connection and exit
        dataInputStream.close();
        inputStream.close();
        socket.close();

        return response;
    }

    // send request using UDP
    private static String sendRequestUDP(String request) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(ip);

        byte[] buf = request.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        byte[] resBuf = new byte[1024];
        DatagramPacket resPacket = new DatagramPacket(resBuf, resBuf.length);

        // set a timeout for unresponsive server
        socket.setSoTimeout(2000);
        socket.receive(resPacket);
        String response = new String(
                resPacket.getData(), 0, resPacket.getLength());
        socket.close();
        return response;
    }

    // check and initialize configuration
    private static void initConfig(String[] args) throws IllegalArgumentException {
        logger = new ClientLogger();

        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "arguments should include [ip or hostname] [port] and [protocol]");
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

        switch (args[2].toLowerCase(Locale.ROOT)) {
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
}