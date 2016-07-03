package org.owlbowl.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Braidner
 */
final class Executor implements Runnable {

    private static final Logger log = LogManager.getLogger(Executor.class.getName());


    private Set<Task> executeList;
    private ExecutorService executorService;

    public Executor(Set<Task> executeList, ExecutorService executorService) {

        this.executeList = executeList;
        this.executorService = executorService;
    }

    @Override
    public void run() {
//        executeList.removeIf(priorityTask -> {
//            execute(priorityTask);
//            return true;
//        });
        if (!executeList.isEmpty()) {
            log.debug("Execute list: {}", executeList.size());
            Set<Task> collected = executeList.stream().peek(this::execute).collect(Collectors.toSet());
            log.debug("Removing executed: {}", collected.size());
            executeList.removeAll(collected);
        }
    }


    public void execute(Task task) {
        executorService.execute(task);
        while (!task.isStarted()) try {
            log.debug("Wait start task {}", task.getDebugIndex());
            TimeUnit.NANOSECONDS.sleep(1);
        } catch (InterruptedException e) {
            //
        }
    }


}
