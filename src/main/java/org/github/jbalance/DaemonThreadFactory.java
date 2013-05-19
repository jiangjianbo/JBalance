package org.github.jbalance;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: jjb
 * DateTime: 2013-05-04 20:45
 */
public class DaemonThreadFactory implements ThreadFactory {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DaemonThreadFactory.class);

    private String namePrefix;
    private ThreadGroup group;
    private AtomicInteger index;

    public DaemonThreadFactory(ThreadGroup group, String namePrefix) {
        index = new AtomicInteger();
        this.group = group;
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r);
        thread.setDaemon(true);
        thread.setName(namePrefix + index.incrementAndGet());
        return thread;
    }

}
