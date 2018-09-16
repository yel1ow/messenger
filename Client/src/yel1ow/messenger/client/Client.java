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
    private static int CLIENT_CONNECTED = 3;
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
//        if(string == null) {
//            printMsg("NULL String received in Client.onReceiveString???????????");
//            return;
//        }
        printMsg("received string: " + string);
        if(!authorized && !(string.charAt(0) == '0' || string.charAt(0) == '1')) {
            printMsg("received string before authorization: " + string.charAt(0));
            return;
        }
        switch (string.charAt(0)) {
            case '0':
                receivedStringType0(tcpConnection, string);
                break;
            case '1':
                receivedStringType1(tcpConnection, string);
                break;
            case '2':
                receivedStringType2(tcpConnection, string);
                break;
            case '3':
                receivedStringType3(tcpConnection, string);
                break;
            case '4':
                receivedStringType4(tcpConnection, string);
                break;
            case '5':
                receivedStringType5(tcpConnection, string);
                break;
        }
    }

    private void receivedStringType0(TCPConnection tcpConnection, String string) {
        final String content = string.substring(1);
        if(content.charAt(0) == '0') {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //authorizationWindow.printError("Authorization successful");
                    mainWindow = new MainWindow(Client.this);
                    authorized = true;
                    mainWindowReady.unlock();
                    authorizationWindow.dispose();
                    receivedStringType5(tcpConnection, content);
                }
            });
        } else {
            SwingUtilities.invokeLater(() -> authorizationWindow.printError(content));
        }
    }

    private void receivedStringType1(TCPConnection tcpConnection, String string) {
        printMsg("received " + string);
        final String content = string.substring(1);
        printMsg("content " + content);
        if(content.equals("")) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //registrationWindow.printError("Registration successful");
                    mainWindow = new MainWindow(Client.this);
                    authorized = true;
                    mainWindowReady.unlock();
                    registrationWindow.dispose();
                }
            });
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    registrationWindow.printError(content);
                }
            });
        }
    }

    private void receivedStringType2(TCPConnection tcpConnection, String string) {
        mainWindow.printMessage(string.substring(1));
    }

    private void receivedStringType3(TCPConnection tcpConnection, String string) {
        mainWindowReady.lock();
        mainWindow.usersTableModel.addData(string.substring(1));
        mainWindow.usersTableModel.fireTableDataChanged();
        mainWindowReady.unlock();
    }

    private void receivedStringType4(TCPConnection tcpConnection, String string) {
        mainWindowReady.lock();
        mainWindow.usersTableModel.removeData(string.substring(1));
        mainWindow.usersTableModel.fireTableDataChanged();
        mainWindowReady.unlock();
    }

    private void receivedStringType5(TCPConnection tcpConnection, String string) {
        Scanner usersOnline = new Scanner(string.substring(1));
        while(usersOnline.hasNext()) {
            mainWindow.usersTableModel.addData(usersOnline.next());
        }
        mainWindow.usersTableModel.fireTableDataChanged();
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
        stringBuilder.append("0");
        stringBuilder.append(login);
        stringBuilder.append(" ");
        stringBuilder.append(password);
        connection.sendMessage(stringBuilder.toString());
    }

    public void registrationButtonPressed(ArrayList<String> fields) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1");
        for(int i = 0; i < fields.size() - 1; i++) {
            stringBuilder.append(fields.get(i));
            stringBuilder.append(" ");
        }
        stringBuilder.append(fields.get(fields.size() - 1));
        connection.sendMessage(stringBuilder.toString());
    }
    public void messageEntered(String message) {
        connection.sendMessage("2" + message);
    }
}
