package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-04 16:32
 */
public interface JBalanceCallback<T> {
    void callback(T arg);
}
