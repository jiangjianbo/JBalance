package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-04 17:45
 */
public interface Task {

    /**
     * execute, if return true, then task continuous, else task terminated
     * @return
     */
    boolean execute();
}
