package org.owlbowl.schedule;

import org.joda.time.DateTime;

import java.util.concurrent.Callable;

/**
 * Created by KuznetsovNE on 30.06.2016.
 */
public interface Scheduler {
    /**
     * Add task to scheduler
     * @param dateTime execution time of task
     * @param callable task for execute
     */
    PriorityTask schedule(DateTime dateTime, Callable callable);

    /**
     * Add task to scheduler
     * @param dateTime execution time of task
     * @param callable task for execute
     */
    PriorityTask schedule(DateTime dateTime, Callable callable, Integer debugIndex);

    /**
     * Shutdown scheduler and scheduler workers
     */
    void shutdown();

    Integer getRunningProcesses();
}
