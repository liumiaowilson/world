package org.wilson.world.event;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public EventType type;
    
    public Map<String, Object> data = new HashMap<String, Object>();
}
