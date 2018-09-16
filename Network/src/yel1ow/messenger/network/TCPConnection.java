package yel1ow.messenger.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionObserver eventObserver;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionObserver eventObserver, String ipAddr, int port) throws IOException {
        this(eventObserver, new Socket(ipAddr, port));
    }

    public TCPConnection(TCPConnectionObserver eventObserver, Socket socket) throws IOException {
        this.eventObserver = eventObserver;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventObserver.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        eventObserver.onReceiveString(TCPConnection.this, in.readLine());
                    }

                } catch (IOException e) {
                    eventObserver.onException(TCPConnection.this, e);
                } finally {
                    eventObserver.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            eventObserver.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventObserver.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return socket.getInetAddress() + ":" + socket.getPort();
    }
}
