package org.owlbowl.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Braidner
 */
public final class DynamicScheduler implements Scheduler {
    private static final Logger log = LogManager.getLogger(DynamicScheduler.class.getName());

    private final Queue<Task> WAIT_QUEUE = new PriorityBlockingQueue<>();
    private final Set<Task> EXECUTION_QUEUE = new ConcurrentSkipListSet<>();
    private final ScheduledExecutorService workerService;

    private AtomicInteger runningProcesses = new AtomicInteger(0);

    public DynamicScheduler(int backlog) {
        int workers = Runtime.getRuntime().availableProcessors();

        workerService = Executors.newScheduledThreadPool(workers * 2);
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < workers * 3; i++) {
            workerService.scheduleAtFixedRate(
                    new Worker(WAIT_QUEUE, EXECUTION_QUEUE, backlog, runningProcesses), 1, 1, TimeUnit.MILLISECONDS
            );
        }

        workerService.scheduleAtFixedRate(
                new org.owlbowl.schedule.Executor(EXECUTION_QUEUE, executorService), 0, 1, TimeUnit.NANOSECONDS
        );
    }

    @Override
    public PriorityTask schedule(DateTime executeTime, Callable task) {
        return schedule(executeTime, task, 0);
    }

    @Override
    public PriorityTask schedule(DateTime executeTime, Callable task, Integer debugIndex) {
        PriorityTask priorityTask = new PriorityTask(executeTime.getMillis(), task, runningProcesses, debugIndex);
        WAIT_QUEUE.add(priorityTask);
        return priorityTask;
    }

    @Override
    public void shutdown() {
        try {
            workerService.shutdown();
            workerService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("No1 cares");
        } finally {
            if (!workerService.isTerminated()) {
                log.warn("Scheduler stopped");
            }
            workerService.shutdownNow();
        }
    }

    @Override
    public Integer getRunningProcesses() {
        return runningProcesses.get();
    }
}
