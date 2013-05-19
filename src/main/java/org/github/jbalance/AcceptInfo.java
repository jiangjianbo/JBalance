package org.github.jbalance;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: jjb
 * DateTime: 2013-05-04 22:11
 */
public class AcceptInfo {

    private InetSocketAddress serverAddress;
    private Socket socket;

    public AcceptInfo(Socket socket, InetSocketAddress serverAddress) {
        this.socket = socket;
        this.serverAddress = serverAddress;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public Socket getSocket() {
        return socket;
    }
}
