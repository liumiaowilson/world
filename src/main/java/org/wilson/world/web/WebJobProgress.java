package org.wilson.world.web;

public class WebJobProgress {
    public WebJobProgressStatus status = WebJobProgressStatus.NotStarted;
    
    public int total;
    
    public int current;
    
    public int percentage;
    
    public volatile boolean stopRequired;
    
    public long startTime;
    
    public long endTime;
}
