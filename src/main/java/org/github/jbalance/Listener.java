package org.github.jbalance;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:05
 */
public interface Listener {
    public static interface ListenerResponse {
        public void doReponse(AcceptInfo socket);
    }

    void listen(InetSocketAddress[] ports, ListenerResponse listenerResponse);
}
