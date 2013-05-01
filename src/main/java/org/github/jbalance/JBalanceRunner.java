package org.github.jbalance;

import java.net.InetAddress;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:37
 */
public class JBalanceRunner {
    private Listener listener;
    private InetAddress[] ports;
    private Handler[] handlers;
    private ThreadPool pool;

    public JBalanceRunner(Listener listener, InetAddress[] listenPorts, Handler[] handlers, boolean threadMode) {
        this.listener = listener;
        this.ports = listenPorts;
        this.handlers = handlers;
        this.pool = threadMode ? new JavaThreadPool(): new LiteThreadPool();
    }

    public void run() {
        listener.listen(ports, new Listener.ListenerReponse(){
            public void doReponse(){
                handleRequest();
            }
        });
    }

    void handleRequest(){

    }

}
