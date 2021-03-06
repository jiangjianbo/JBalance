package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-01 21:23
 */
public interface ThreadPool {
    void execute(Task task);

    void waitUntilFinish();
}
