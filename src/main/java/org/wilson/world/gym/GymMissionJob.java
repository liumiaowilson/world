package org.wilson.world.gym;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.GymManager;
import org.wilson.world.manager.MissionManager;
import org.wilson.world.manager.StatsManager;
import org.wilson.world.mission.Mission;
import org.wilson.world.mission.MissionType;
import org.wilson.world.schedule.DefaultJob;

public class GymMissionJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(GymMissionJob.class);
    
    @Override
    public void execute() {
        logger.info("Added new gym missions");
        
        List<Mission> missions = MissionManager.getInstance().getMissionsOfType(MissionType.Gym);
        int defaultSize = GymManager.getInstance().getGymMissionDefaultSize();
        int new_size = 0;
        
        if(missions.size() >= defaultSize) {
            new_size = 1;
        }
        else {
            new_size = defaultSize - missions.size();
        }
        
        Map<String, Integer> eventData = StatsManager.getInstance().getEventTypeStats();
        List<String> topEvents = StatsManager.getInstance().getTopEvents(ConfigManager.getInstance().getConfigAsInt("mission.event.top.size", 10), eventData);
        
        for(int i = 0; i < new_size; i++) {
            Mission mission = GymManager.getInstance().generateGymMission(eventData, topEvents);
            if(mission != null) {
                MissionManager.getInstance().addMission(mission);
            }
        }
    }

}
