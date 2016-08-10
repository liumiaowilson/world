package org.wilson.world.web;

public interface WebJobMonitor {
    public void start(int totalSteps);
    
    public void progress(int steps);
    
    public void succeed();
    
    public void fail();
}
