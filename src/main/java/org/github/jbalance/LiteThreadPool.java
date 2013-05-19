package org.github.jbalance;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: jjb
 * DateTime: 2013-05-01 21:24
 */
public class LiteThreadPool implements ThreadPool {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LiteThreadPool.class);

    private ConcurrentLinkedQueue<Task> tasks;
    private ExecutorService executor;

    public LiteThreadPool() {
        this.tasks = new ConcurrentLinkedQueue<Task>();
        final ThreadGroup group = new ThreadGroup("lite-thread-group");
        this.executor = Executors.newFixedThreadPool(10, new DaemonThreadFactory(group, "lite-thread-executor-"));
        startGuard();
    }

    /**
     * get task from queue, and submit to executor
     */
    private void startGuard() {
        Utils.background("lite-thread-guard", new Runnable() {
            @Override
            public void run() {
                while(true){
                    Task task = tasks.poll();
                    if( task != null ){
                        logger.debug("pool task {}", task);
                        submitTask(task);
                    }
                }
            }
        });
    }

    private void submitTask(final Task task) {
        logger.debug("submit task {}", task);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // if task return true, then add into queue
                if(task.execute()){
                    tasks.add(task);
                    logger.debug("task {} append again.", task);
                }else{
                    logger.debug("task {} finished!", task);
                }
            }
        });
    }

    @Override
    public void execute(Task task) {
        if( task != null && !tasks.contains(task)){
            tasks.add(task);
            logger.debug("add task {}", task);
        }
    }

    @Override
    public void waitUntilFinish() {
        if( tasks.size() == 0 )
            executor.shutdownNow();
        Utils.shutdownAndAwaitTermination(executor,0);
    }
}
