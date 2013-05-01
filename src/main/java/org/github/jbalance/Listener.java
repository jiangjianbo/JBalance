package org.github.jbalance;

import java.net.InetAddress;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:05
 */
public interface Listener {
    public static interface ListenerReponse {
        public void doReponse();
    }

    void listen(InetAddress[] ports, ListenerReponse listenerReponse);
}
