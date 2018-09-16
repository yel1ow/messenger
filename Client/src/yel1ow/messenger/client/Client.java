package yel1ow.messenger.client;

import yel1ow.messenger.client.GUI.AuthorizationWindow;
import yel1ow.messenger.client.GUI.MainWindow;
import yel1ow.messenger.client.GUI.PortInputWindow;
import yel1ow.messenger.client.GUI.RegistrationWindow;
import yel1ow.messenger.network.TCPConnection;
import yel1ow.messenger.network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends JFrame implements TCPConnectionObserver {

    public static final String NAME = "Skype v3.22";
    private static final String IP_ADDR = "0.tcp.ngrok.io";
    private int Port = 3228;

    public AuthorizationWindow authorizationWindow;
    public RegistrationWindow registrationWindow;
    private MainWindow mainWindow;

    private static int WIDTH = 640;
    private static int HEIGHT = 480;

    private final int AUTHORIZATION = 0;
    private final int REGISTRATION = 1;
    private final int MESSAGE = 2;
    private final int CLIENT_CONNECTED = 3;
    private final int CLIENT_DISCONNECTED = 4;

    final Lock mainWindowReady = new ReentrantLock();
    private boolean authorized = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("nelox");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private Client() {
        mainWindowReady.lock();
        new PortInputWindow(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String string) {
        printMsg("received string: " + string);
        int spaceIndex = string.indexOf(" ");
        int messageId = Integer.parseInt(string.substring(0, spaceIndex));
        String receivedString = string.substring(spaceIndex + 1);
        if(!authorized && !(messageId == 0 || messageId == 1)) {
            printMsg("received string before authorization: " + Integer.toString(messageId));
            return;
        }
        switch (messageId) {
            case AUTHORIZATION:
                receivedAuthorization(tcpConnection, receivedString);
                break;
            case REGISTRATION:
                receivedRegistration(tcpConnection, receivedString);
                break;
            case MESSAGE:
                receivedMessage(tcpConnection, receivedString);
                break;
            case CLIENT_CONNECTED:
                receivedClientConnected(tcpConnection, receivedString);
                break;
            case CLIENT_DISCONNECTED:
                receivedClientDisconnected(tcpConnection, receivedString);
                break;
        }
    }

    private void receivedAuthorization(TCPConnection tcpConnection, String string) {
        if(string.charAt(0) == '0') {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mainWindow = new MainWindow(Client.this);
                    authorized = true;
                    mainWindowReady.unlock();
                    authorizationWindow.dispose();
                    addUsersToForm(tcpConnection, string.substring(1));
                }
            });
        } else {
            SwingUtilities.invokeLater(() -> authorizationWindow.printError(string));
        }
    }

    private void addUsersToForm(TCPConnection tcpConnection, String string) {
        Scanner usersOnline = new Scanner(string);
        while(usersOnline.hasNext()) {
            mainWindow.usersTableModel.addData(usersOnline.next());
        }
        mainWindow.usersTableModel.fireTableDataChanged();
    }

    private void receivedRegistration(TCPConnection tcpConnection, String string) {
        printMsg("string " + string);
        if(string.charAt(0) == '0') {
            Scanner scanner = new Scanner(string.substring(1));
            String login = scanner.next();
            String password = scanner.next();
            connection.sendMessage(Integer.toString(AUTHORIZATION) + " " + login + " " + password);
            registrationWindow.dispose();
        } else {
            registrationWindow.printError(string);
        }
    }

    private void receivedMessage(TCPConnection tcpConnection, String string) {
        mainWindow.printMessage(string);
    }

    private void receivedClientConnected(TCPConnection tcpConnection, String string) {
        mainWindowReady.lock();
        mainWindow.usersTableModel.addData(string);
        mainWindow.usersTableModel.fireTableDataChanged();
        mainWindowReady.unlock();
    }

    private void receivedClientDisconnected(TCPConnection tcpConnection, String string) {
        mainWindowReady.lock();
        mainWindow.usersTableModel.removeData(string);
        mainWindow.usersTableModel.fireTableDataChanged();
        mainWindowReady.unlock();
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        mainWindow.printMessage(e.getMessage());
    }

//    public synchronized void printMsg(final String msg) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                log.append(msg + "\n");
//                log.setCaretPosition(log.getDocument().getLength());
//            }
//        });
//    }

    public synchronized void printMsg(final String msg) {
        SwingUtilities.invokeLater(() -> {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            });
    }

    public boolean onPortInput(int Port) {
        this.Port = Port;
        try {
            connection = new TCPConnection(this, "127.0.0.1", Port);
        } catch (Exception e) {
            printMsg("Connection exception");
            return false;
        }
        authorizationWindow = new AuthorizationWindow(this);
        return true;
    }

    public void logInButtonPressed(String login, String password) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Integer.toString(AUTHORIZATION) + " ");
        stringBuilder.append(login);
        stringBuilder.append(" ");
        stringBuilder.append(password);
        connection.sendMessage(stringBuilder.toString());
    }

    public void registrationButtonPressed(ArrayList<String> fields) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Integer.toString(REGISTRATION) + " ");
        for(int i = 0; i < fields.size() - 1; i++) {
            stringBuilder.append(fields.get(i));
            stringBuilder.append(" ");
        }
        stringBuilder.append(fields.get(fields.size() - 1));
        connection.sendMessage(stringBuilder.toString());
    }
    public void messageEntered(String message) {
        connection.sendMessage(Integer.toString(MESSAGE) + " " + message);
    }
}
