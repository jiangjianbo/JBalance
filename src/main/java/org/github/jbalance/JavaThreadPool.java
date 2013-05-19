package org.github.jbalance;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: jjb
 * DateTime: 2013-05-01 21:23
 */
public class JavaThreadPool implements ThreadPool {

    private ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory(new ThreadGroup("java-thread"), "java-thread-"));

    @Override
    public void execute(final Task task) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while(task.execute());
            }
        });
    }

    @Override
    public void waitUntilFinish() {
        Utils.shutdownAndAwaitTermination(executor, 0);
    }
}
