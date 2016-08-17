package org.wilson.world.idea;

import org.wilson.world.event.EventType;
import org.wilson.world.model.Idea;

public interface IdeaConverter {
    public String getName();
    
    public Object convert(Idea idea);
    
    public void save(Object converted);
    
    public EventType getEventType();
}
