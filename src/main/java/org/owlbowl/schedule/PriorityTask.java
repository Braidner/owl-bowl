package org.owlbowl.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by KuznetsovNE on 01.07.2016.
 */
final class PriorityTask extends FutureTask implements Comparable<PriorityTask>, Task {

    private static final Logger log = LogManager.getLogger(PriorityTask.class.getName());

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0); //TODO  auto-refresh

    private final Long startTime;
    private final Callable task;
    private final Integer index;
    private final AtomicInteger runningProcesses;
    private int debugIndex;

    private AtomicBoolean started = new AtomicBoolean(false);
    private boolean finished = false;
    private Object result = null;

    PriorityTask(final Long startTime, final Callable task, final AtomicInteger runningProcesses) {
        super(task);
        this.startTime = startTime;
        this.task = task;
        this.index = SEQUENCE.getAndIncrement();
        this.runningProcesses = runningProcesses;
        this.debugIndex = 0;
    }

    PriorityTask(final Long startTime, final Callable task, final AtomicInteger runningProcesses, final int debugIndex) {
        this(startTime, task, runningProcesses);
        this.debugIndex = debugIndex;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    public Object call() throws Exception {

        return result;
    }

    @Override
    public int compareTo(PriorityTask o) {
        int compare = startTime.compareTo(o.startTime);
        if (compare == 0) {
            compare = index.compareTo(o.index);
        }
        return compare;
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public boolean isDone() {
        return finished;
    }

    @Override
    public Object get() {
        if (!finished) throw new RuntimeException("Task wasn't executed");
        return result;
    }

    public int getDebugIndex() {
        return debugIndex;
    }

    @Override
    public void run() {
        log.debug("Start running task: {}", debugIndex);
        started.set(true);
        runningProcesses.incrementAndGet();
        try {
            super.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void set(Object result) {
        super.set(result);
        this.result = result;
    }

    @Override
    protected void done() {
        this.finished = true;
        runningProcesses.decrementAndGet();
    }

    public Integer getIndex() {
        return index;
    }
}