package org.wilson.world.mission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.DiceManager;

public class DefaultMissionEventProvider implements MissionEventProvider {
    private Map<String, Integer> last = null;
    private List<String> eventTypes = null;
    
    @Override
    public String nextEventTypeName(Map<String, Integer> data) {
        if(data != last) {
            last = data;
            eventTypes = new ArrayList<String>(data.keySet());
        }
        
        if(eventTypes == null || eventTypes.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(eventTypes.size());
        return eventTypes.get(n);
    }

    @Override
    public int getEventTypeDefaultValue(String name) {
        return 1;
    }

}
