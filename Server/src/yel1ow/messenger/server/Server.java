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

    private static final String AUTHORIZATION = "0";
    private static final String REGISTRATION = "1";
    private static final String MESSAGE = "2";

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
        printMsg(string);
        switch (string.charAt(0)) {
            case '0':
                receivedStringType0(tcpConnection, string);
                break;
            case '1':
                receivedStringType1(tcpConnection, string);
                break;
            case '2':
                receivedStringType2(tcpConnection, string);
            default:
                break;
        }
    }

    private void receivedStringType0(TCPConnection tcpConnection, String string) {
//        for(int i = 0; i < authorized.size(); i++) {
//            printMsg("aut " + i + " " + authorized.get(i));
//        }
        if(authorized.get(connections.indexOf(tcpConnection)) != 0) {
            tcpConnection.sendMessage(AUTHORIZATION + "already authorized");
            return;
        }
        Scanner scanner = new Scanner(string.substring(1));
        String login = scanner.next();
        String password = scanner.next();
        printMsg("Received login:" + login + " password: " + password);

        String result = DBConnection.checkLoginAndPassword(login, password);
        Scanner resultScanner = new Scanner(result);
        if(resultScanner.hasNextInt()) {
            int id = resultScanner.nextInt();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(AUTHORIZATION + "0");
            for(int i = 0; i < authorized.size(); i++) {
                if(authorized.get(i) != 0)
                    stringBuilder.append(DBConnection.getNameById(authorized.get(i)) + " ");
            }
            tcpConnection.sendMessage(stringBuilder.toString());
            sendToAllConnectionsExcept1(tcpConnection,"3" + DBConnection.getNameById(id));
            authorized.set(connections.indexOf(tcpConnection), id);
        } else {
            tcpConnection.sendMessage(AUTHORIZATION + result);
        }
    }

    private void receivedStringType1(TCPConnection tcpConnection, String string) {
        if(authorized.get(connections.indexOf(tcpConnection)) != 0)
            return;
        Scanner scanner = new Scanner(string.substring(1));
        String name = scanner.next();
        String surname = scanner.next();
        String login = scanner.next();
        String password = scanner.next();

        String result = DBConnection.registerNewUser(name, surname, login, password);
        if(result.equals("success")) {
            tcpConnection.sendMessage(REGISTRATION);
            Scanner resultScanner = new Scanner(DBConnection.checkLoginAndPassword(login, password));
            if(resultScanner.hasNextInt()) {
                int id = resultScanner.nextInt();
                sendToAllConnectionsExcept1(tcpConnection,"3" + DBConnection.getNameById(id));
                authorized.set(connections.indexOf(tcpConnection), id);
                authorized.set(connections.indexOf(tcpConnection), id);
            } else {
                printMsg("ERROR login&password not returns id!!!");
            }
        } else {
            tcpConnection.sendMessage(REGISTRATION + result);
        }
    }

    private void receivedStringType2(TCPConnection tcpConnection, String string) {
        sendToAllConnections(MESSAGE + DBConnection.getNameById(authorized.get(connections.indexOf(tcpConnection))) + ": " + string.substring(1));
        System.out.println("Received and sended");
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        sendToAllConnectionsExcept1(tcpConnection, "4" + DBConnection.getNameById(authorized.get(connections.indexOf(tcpConnection))));
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}