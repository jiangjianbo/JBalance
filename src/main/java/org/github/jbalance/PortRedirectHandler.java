package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:44
 */
public class PortRedirectHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(PortRedirectHandler.class);

    private InetSocketAddress sourcePort, destinationPort;
    private Socket destination;
    private StreamPipe pipe;
    private boolean inputClose = false, outputClose = false;

    @Override
    public boolean canHandle(RequestInfo request) {
        return request.getRemotePort() == sourcePort.getPort()
                && (sourcePort.getAddress().isAnyLocalAddress() || sourcePort.getAddress().equals(request.getRemoteAddress()) );
    }

    @Override
    public boolean prepare(RequestInfo request) {
        try {
            logger.debug("request from {} to {}", request.getSocket().getInetAddress(), destinationPort);
            destination = new Socket(destinationPort.getAddress(), destinationPort.getPort());
            pipe = new StreamPipe(request.getInputStream(), request.getOutputStream(), destination.getInputStream(), destination.getOutputStream());
            return true;
        } catch (IOException e) {
            logger.error("socket connect error", e);
            return false;
        }
    }

    @Override
    public boolean handle(RequestInfo request) {
        if( !inputClose )
        try {
            logger.debug("pipe input");
            pipe.pipeInput();
        } catch (StreamException e) {
            inputClose = true;
            logger.error("pipe input error", e);

            Utils.close(request.getInputStream());
            try {
                Utils.close(destination.getOutputStream());
            } catch (IOException e1) {
                logger.error("get destination output stream error", e1);
            }
        }

        if( !outputClose )
        try {
            logger.debug("pipe output");
            pipe.pipeOutput();
        } catch (StreamException e) {
            outputClose = true;
            logger.error("pipe output error", e);

            Utils.close(request.getOutputStream());
            try {
                Utils.close(destination.getInputStream());
            } catch (IOException e1) {
                logger.error("get destination input stream error", e1);
            }
        }

        if( inputClose && outputClose ){
            logger.debug("close sockets");
            Utils.close(destination);
            Utils.close(request.getSocket());
            return false;
        } else
            return true;
    }

    public InetSocketAddress getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(InetSocketAddress sourcePort) {
        this.sourcePort = sourcePort;
    }

    public InetSocketAddress getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(InetSocketAddress destinationPort) {
        this.destinationPort = destinationPort;
    }


}
