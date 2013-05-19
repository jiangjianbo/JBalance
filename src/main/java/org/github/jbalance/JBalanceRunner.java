package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:37
 */
public class JBalanceRunner {

    private static final Logger logger = LoggerFactory.getLogger(JBalanceRunner.class);

    private Listener listener;
    private InetSocketAddress[] ports;
    private Dispatcher dispatcher;

    public JBalanceRunner(Listener listener, InetSocketAddress[] listenPorts, Dispatcher dispatcher) {
        this.listener = listener;
        this.ports = listenPorts;
        this.dispatcher = dispatcher;
    }

    public void run() {
        listener.listen(ports, new Listener.ListenerResponse(){
            public void doReponse(AcceptInfo request){
                dispatcher.dispatch(request);
            }
        });
    }



}
