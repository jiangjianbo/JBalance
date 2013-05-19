package org.github.jbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jjb
 * DateTime: 2013-05-04 16:28
 */
public class DefaultDispatcher implements Dispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDispatcher.class);

    private Handler[] handlers = null;
    private ThreadPool dispatchpool, pool;
    private Map<Object, Handler> index = new HashMap<Object, Handler>();

    public DefaultDispatcher(){
        dispatchpool = new LiteThreadPool();
    }

    @Override
    public void dispatch(final AcceptInfo request) {
        // create thread for dispatch
        dispatchpool.execute(new Task(){

            @Override
            public boolean execute() {
                try {
                    final RequestInfo info = collectInfo(request);
                    for(final Handler handle : handlers)
                        if( handle.canHandle(info) ){
                            // create thread for handle
                            if( handle.prepare(info) )
                                pool.execute(new Task() {
                                    @Override
                                    public boolean execute() {
                                        return handle.handle(info);
                                    }
                                });
                            return false;
                        }
                } catch (IOException e) {
                    logger.error("collect socket info error", e);
                    Utils.close(request.getSocket());
                }
                return false;
            }
        });
    }

    public RequestInfo collectInfo(AcceptInfo acceptInfo) throws IOException{
        RequestInfo info = new RequestInfo(acceptInfo.getSocket(), acceptInfo.getServerAddress());
        info.collect();
        return info;
    }

    @Override
    public void eachHandler(JBalanceCallback<Handler> callback) {
        for(Handler handler: handlers)
            callback.callback(handler);
    }

    @Override
    public void setHandlers(Handler[] handlers) {
        this.handlers = Arrays.copyOf(handlers, handlers.length);
    }

    @Override
    public ThreadPool getPool() {
        return pool;
    }

    @Override
    public void setPool(ThreadPool threadPool) {
        this.pool = threadPool;
    }
}
