package org.owlbowl.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Braidner
 */
final class Worker implements Runnable {

    private static final Logger log = LogManager.getLogger(Worker.class.getName());


    private final Queue<Task> waitRoom;
    private final Set<Task> executionRoom;
    private final AtomicInteger runningProcesses;
    private final int backlog;

    Worker(Queue<Task> waitRoom, Set<Task> executionRoom, int backlog, AtomicInteger runningProcesses) {
        this.waitRoom = waitRoom;
        this.executionRoom = executionRoom;
        this.backlog = backlog;
        this.runningProcesses = runningProcesses;
    }

    @Override
    public void run() {
        int size = waitRoom.size();
        if (size > 0) {
            Task task = waitRoom.poll();
            if (!task.isStarted()) {
                if (backlog <= runningProcesses.get() || System.currentTimeMillis() >= task.getStartTime()) {
                    executionRoom.add(task);
                } else {
                    waitRoom.add(task);
                }
            }
        }
    }
}
