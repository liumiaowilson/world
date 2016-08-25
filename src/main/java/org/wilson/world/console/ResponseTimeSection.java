package org.wilson.world.console;

public class ResponseTimeSection {
    public String name;
    
    public long start;
    
    public long end;
    
    public int count;
    
    public boolean contains(long time) {
        return this.start <= time && this.end >= time;
    }
}
