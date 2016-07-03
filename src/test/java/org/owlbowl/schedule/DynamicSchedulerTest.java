package org.owlbowl.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Braidner
 */
public class DynamicSchedulerTest {

    private static final Logger log = LogManager.getLogger(DynamicSchedulerTest.class.getName());

    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        scheduler = new DynamicScheduler(100);
    }

    @Test
    public void scheduleWithDifferentTime() throws Exception {
        scheduler.schedule(new DateTime().plusSeconds(9), () -> {log.info("I'm 5");return 5;});
        scheduler.schedule(new DateTime().plusSeconds(1), () -> {log.info("I'm 1");return 1;});
        scheduler.schedule(new DateTime().plusSeconds(3), () -> {log.info("I'm 3");return 3;});
        scheduler.schedule(new DateTime().plusSeconds(4), () -> {log.info("I'm 4");return 4;});
        scheduler.schedule(new DateTime().plusSeconds(2), () -> {log.info("I'm 2");return 2;});
        Thread.sleep(10000);
    }

    @Test
    public void scheduleWithSameTime() throws Exception {
        DateTime executionTime = new DateTime().plusSeconds(2);
        for (int i = 0; i < 10000; i++) { //10000 requests per second
            final int priority = i;
            scheduler.schedule(executionTime, () -> {
                String expectedExecution = executionTime.toString("hh:mm:ss.SSSZZ");
                String actualExecution = new DateTime().toString("hh:mm:ss.SSSZZ");
                log.info("I'm {}. I'll be executed at {} now {}. Running: {}",
                        priority,
                        expectedExecution,
                        actualExecution,
                        scheduler.getRunningProcesses()
                );
                return 1;
            }, i);
        }
        Thread.sleep(10000);
    }

    @Test
    public void scheduleStress() throws Exception {
        for (int i = 0; i < 10; i++) { //total butch requests
            for (int j = 0; j < 100; j++) { //requests per second
                final int priority = j;
                DateTime executionTime = new DateTime().plusSeconds(i);
                scheduler.schedule(executionTime, () -> {
                    String expectedExecution = executionTime.toString("hh:mm:ss");
                    String actualExecution = new DateTime().toString("hh:mm:ss");
                    log.info("I'm {}. I'll be executed at {} now {}",
                            priority,
                            expectedExecution,
                            actualExecution);
                    return 1;
                });
            }
        }

        Thread.sleep(10000);
    }

}