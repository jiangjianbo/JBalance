package org.github.jbalance;

/**
 * User: jjb
 * DateTime: 2013-05-05 00:14
 */
public class JavaThreadPoolTest {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JavaThreadPoolTest.class);

    long finish = 0;
    private static final int MAX_TASKS = 1000;

    @org.testng.annotations.Test
    public void testExecute() throws Exception {
        JavaThreadPool pool = new JavaThreadPool();
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
        System.out.println("start=" + start + ", finish=" + finish + ", ms=" + (finish-start));
    }
}
