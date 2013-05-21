package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-20 05:49
 */
public interface AcceptPolicy {

    Priority check(RequestInfo info);

    public static enum Priority{NORMAL, DELAY, REFUSE, IMMEDIATELY}

}
