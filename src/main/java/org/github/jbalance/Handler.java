package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-01 20:12
 */
public interface Handler {

    /**
     * check
     * @param request
     * @return
     */
    boolean canHandle(RequestInfo request);

    /**
     * call before {@link #handle}, if return false, then abort
     * @param request
     * @return
     */
    boolean prepare(RequestInfo request);

    /**
     * repeat until return false
     * @param request
     * @return
     */
    boolean handle(RequestInfo request);

}
