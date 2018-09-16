package yel1ow.messenger.server;

import yel1ow.messenger.db.DBConnection;
import yel1ow.messenger.network.TCPConnection;
import yel1ow.messenger.network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server extends JFrame implements TCPConnectionObserver {

    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();
    private final ArrayList<Integer> authorized = new ArrayList<>();

    private final JTextArea log = new JTextArea();
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final int AUTHORIZATION = 0;
    private final int REGISTRATION = 1;
    private final int MESSAGE = 2;
    private final int CLIENT_CONNECTED = 3;
    private final int CLIENT_DISCONNECTED = 4;

    private Server() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setSize(WIDTH, HEIGHT);
                setLocationRelativeTo(null);

                log.setEditable(false);
                log.setLineWrap(true);
                add(log, BorderLayout.CENTER);

                printMsg("Server running...");

                setVisible(true);
            }
        });

        if (!DBConnection.connect()) {
            throw new RuntimeException("Connection to DB exception");
        }

        try(ServerSocket serverSocket = new ServerSocket(3228)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    printMsg("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        authorized.add(0);
        //sendToAll(Client connected);
        printMsg("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String string) {
        printMsg("Received string: " + string);
        int spaceIndex = string.indexOf(" ");
        int messageId = Integer.parseInt(string.substring(0, spaceIndex));
        String receivedString = string.substring(spaceIndex + 1);
        switch (messageId) {
            case AUTHORIZATION:
                receivedAuthorization(tcpConnection, receivedString);
                break;
            case REGISTRATION:
                receivedRegistration(tcpConnection, receivedString);
                break;
            case MESSAGE:
                receivedMessage(tcpConnection, receivedString);
            default:
                break;
        }
    }

    private void receivedAuthorization(TCPConnection tcpConnection, String string) {
        if(authorized.get(connections.indexOf(tcpConnection)) != 0) {
            tcpConnection.sendMessage(Integer.toString(AUTHORIZATION) + " " + "already authorized");
            return;
        }
        Scanner scanner = new Scanner(string);
        String login = scanner.next();
        String password = scanner.next();
        printMsg("Received login:" + login + " password: " + password);

        String result = DBConnection.checkLoginAndPassword(login, password);
        Scanner resultScanner = new Scanner(result);
        if(resultScanner.hasNextInt()) {
            int id = resultScanner.nextInt();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Integer.toString(AUTHORIZATION) + " 0");
            for(int i = 0; i < authorized.size(); i++) {
                if(authorized.get(i) != 0)
                    stringBuilder.append(DBConnection.getNameById(authorized.get(i)) + " ");
            }
            tcpConnection.sendMessage(stringBuilder.toString());
            sendToAllConnectionsExcept1(tcpConnection,Integer.toString(CLIENT_CONNECTED) + " " + DBConnection.getNameById(id));
            authorized.set(connections.indexOf(tcpConnection), id);
        } else {
            tcpConnection.sendMessage(Integer.toString(AUTHORIZATION) + " " + result);
        }
    }

    private void receivedRegistration(TCPConnection tcpConnection, String string) {
        if(authorized.get(connections.indexOf(tcpConnection)) != 0)
            return;
        Scanner scanner = new Scanner(string);
        String name = scanner.next();
        String surname = scanner.next();
        String login = scanner.next();
        String password = scanner.next();

        String result = DBConnection.registerNewUser(name, surname, login, password);
        if(result.equals("success")) {
            tcpConnection.sendMessage(Integer.toString(REGISTRATION) + " 0" + login + " " + password);
        } else {
            tcpConnection.sendMessage(Integer.toString(REGISTRATION) + " " + result);
        }
    }

    private void receivedMessage(TCPConnection tcpConnection, String string) {
        sendToAllConnections(Integer.toString(MESSAGE) + " " + DBConnection.getNameById(authorized.get(connections.indexOf(tcpConnection))) + ": " + string.substring(1));
        System.out.println("Received and sended");
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        sendToAllConnectionsExcept1(tcpConnection, Integer.toString(CLIENT_DISCONNECTED) + " " + DBConnection.getNameById(authorized.get(connections.indexOf(tcpConnection))));
        authorized.remove(connections.indexOf(tcpConnection));
        connections.remove(tcpConnection);
        printMsg("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String string) {
        final int size = connections.size();
        for(int i = 0; i < size; i++) {
            connections.get(i).sendMessage(string);
        }
    }

    private void sendToAllConnectionsExcept1(TCPConnection tcpConnection, String string) {
        final int size = connections.size();
        for(int i = 0; i < size; i++) {
            if(!connections.get(i).equals(tcpConnection)) {
                connections.get(i).sendMessage(string);
            }
        }
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            });
    }
}