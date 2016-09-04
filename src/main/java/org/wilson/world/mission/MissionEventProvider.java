package org.wilson.world.mission;

import java.util.Map;

public interface MissionEventProvider {
    public String nextEventTypeName(Map<String, Integer> data);
    
    public int getEventTypeDefaultValue(String name);
}
