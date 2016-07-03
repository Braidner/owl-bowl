package org.owlbowl.schedule;

/**
 * Created by KuznetsovNE on 01.07.2016.
 */
public interface Task extends Runnable {
    boolean isDone();
    boolean isStarted();
    long getStartTime();
    Object get();

    default int getDebugIndex() {
        return 0;
    }
}
