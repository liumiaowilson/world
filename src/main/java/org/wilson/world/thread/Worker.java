package org.wilson.world.thread;

public interface Worker extends Runnable {
    public long getPeriods();
    
    public long getTotalTime();
    
    public long getWorkingTime();
    
    public void setStopped(boolean stopped);
    
    public boolean isStopped();
}
