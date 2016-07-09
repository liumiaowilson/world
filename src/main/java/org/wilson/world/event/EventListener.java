package org.wilson.world.event;

public interface EventListener {
    public boolean isAsync();
    
    public void handle(Event event);
}
