package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:42
 */
public class DefaultListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultListener.class);

    ArrayList < ServerSocket > serverSockets = new ArrayList<ServerSocket>();
    ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public void listen(InetSocketAddress[] ports, ListenerResponse listenerResponse) {
        try {
            for(InetSocketAddress addr : ports){
                logger.info("listen {}", addr);
                listenAddress(addr, listenerResponse);
            }
        } catch (IOException e) {
            logger.error("listen error, shutdown and close sockets", e);
            executor.shutdown();
            for(ServerSocket socket: serverSockets)
                try {
                    logger.info("close server socket {}", socket);
                    socket.close();
                } catch (IOException e1) {
                    logger.debug("close socket exception ", e1);
                }
        }
    }

    private void listenAddress(final InetSocketAddress addr, final ListenerResponse response) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(addr.getPort(), 0, addr.getAddress());
        serverSockets.add(serverSocket);

        // one listen one thread
        executor.submit(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Socket socket = serverSocket.accept();
                        response.doReponse(new AcceptInfo(socket, addr), DefaultListener.this);
                    } catch (IOException e) {
                        logger.error("accept socket error", e);
                    }
                }
            }
        });

    }
}
