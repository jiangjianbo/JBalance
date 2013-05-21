package org.github.jbalance;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:05
 */
public interface Listener {
    Collection<AcceptPolicy> getPolicies();

    public static interface ListenerResponse {
        public void doReponse(AcceptInfo socket, Listener listener);
    }

    void listen(InetSocketAddress[] ports, ListenerResponse listenerResponse);
}
