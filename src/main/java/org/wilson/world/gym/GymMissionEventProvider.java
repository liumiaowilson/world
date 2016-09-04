package org.wilson.world.gym;

import java.util.Map;

import org.wilson.world.manager.GymManager;
import org.wilson.world.mission.MissionEventProvider;

public class GymMissionEventProvider implements MissionEventProvider {

    @Override
    public String nextEventTypeName(Map<String, Integer> data) {
        GymItem item = GymManager.getInstance().randomGymItem(true);
        if(item != null && item.event != null) {
            return item.event.name();
        }
        else {
            return null;
        }
    }

    @Override
    public int getEventTypeDefaultValue(String name) {
        return 1;
    }

}
