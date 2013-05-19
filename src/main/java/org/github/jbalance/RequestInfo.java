package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: jjb
 * DateTime: 2013-05-04 15:04
 */
public class RequestInfo {
    private static final Logger logger = LoggerFactory.getLogger(RequestInfo.class);

    private Socket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    private InetAddress localAddress;
    private int localPort;
    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;
    private int bufferSize;
    private InputStream alterInputStream;
    private OutputStream alterOutputStream;
    private InetSocketAddress serverAddress;

    public RequestInfo(Socket socket, InetSocketAddress serverAddress) {
        this.socket = socket;
        buffer = new byte[BUFFER_SIZE];
        bufferSize = 0;
        this.serverAddress = serverAddress;
    }

    public void collect() throws IOException {
        remoteAddress = socket.getInetAddress();
        remotePort = socket.getPort();
        localAddress = socket.getLocalAddress();
        localPort = socket.getLocalPort();
        // read buffer
        InputStream ins = socket.getInputStream();
        bufferSize = ins.read(buffer);
        alterInputStream = new SequenceInputStream(new ByteArrayInputStream(buffer,  0, bufferSize), new BufferedInputStream(ins));
        alterOutputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public InputStream getInputStream() {
        return alterInputStream;
    }

    public OutputStream getOutputStream() {
        return alterOutputStream;
    }

}
