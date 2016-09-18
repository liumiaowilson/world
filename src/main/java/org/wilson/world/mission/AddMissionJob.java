package org.wilson.world.mission;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.MissionManager;
import org.wilson.world.manager.StatsManager;
import org.wilson.world.schedule.DefaultJob;

public class AddMissionJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(AddMissionJob.class);
    
    @Override
    public void execute() {
        logger.info("Added new missions");
        
        List<Mission> missions = MissionManager.getInstance().getMissionsOfType(MissionType.Default);
        int defaultSize = MissionManager.getInstance().getMissionDefaultSize();
        int new_size = 0;
        
        if(missions.size() >= defaultSize) {
            new_size = 1;
        }
        else {
            new_size = defaultSize - missions.size();
        }
        
        Map<String, Integer> data = StatsManager.getInstance().getEventTypeStats();
        int topn = ConfigManager.getInstance().getConfigAsInt("mission.event.top.size", 10);
        List<String> topEvents = StatsManager.getInstance().getTopEvents(topn);
        
        for(int i = 0; i < new_size; i++) {
            Mission mission = MissionManager.getInstance().generateMission(data, topEvents);
            if(mission != null) {
                MissionManager.getInstance().addMission(mission);
            }
        }
    }

}
