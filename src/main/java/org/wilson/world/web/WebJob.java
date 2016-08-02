package org.wilson.world.web;

public interface WebJob {
    public void setId(int id);
    
    public int getId();
    
    public String getName();
    
    public String getDescription();
    
    public int getPeriod();
    
    public void run() throws Exception;
}
