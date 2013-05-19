package org.github.jbalance;

import java.io.IOException;
import java.net.Socket;

/**
 * User: jjb
 * DateTime: 2013-05-04 16:24
 */
public interface Dispatcher {

    void dispatch(AcceptInfo acceptInfo);

    RequestInfo collectInfo(AcceptInfo acceptInfo) throws IOException;

    void eachHandler(JBalanceCallback<Handler> callback);
    void setHandlers(Handler[] handlers);

    ThreadPool getPool();
    void setPool(ThreadPool threadPool);
}
