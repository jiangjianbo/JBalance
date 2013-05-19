package org.github.jbalance;

import org.testng.annotations.Test;

/**
 * User: jjb
 * DateTime: 2013-05-05 00:37
 */
public class LiteThreadPoolTest {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LiteThreadPoolTest.class);

    long finish = 0;
    private static final int MAX_TASKS = 1000;

    @Test
    public void testExecute() throws Exception {
        LiteThreadPool pool = new LiteThreadPool();
        long start = System.currentTimeMillis();
        for(int i = 0; i < MAX_TASKS; ++i) {
            pool.execute(new Task() {
                @Override
                public boolean execute() {
                    finish = System.currentTimeMillis();
                    return false;
                }
            });
        }
        pool.waitUntilFinish();
        System.out.println("start=" + start + ", finish=" + finish + ", ms=" + (finish - start));

    }
}
