package org.wilson.world.mission;

import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.MissionManager;
import org.wilson.world.schedule.DefaultJob;
import org.wilson.world.util.TimeUtils;

public class DelMissionJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(DelMissionJob.class);
    
    @Override
    public void execute() {
        logger.info("Expired missions");
        
        int hours = ConfigManager.getInstance().getConfigAsInt("mission.expire.default.hours", 48);
        long now = System.currentTimeMillis();
        List<Mission> missions = MissionManager.getInstance().getMissions();
        for(Mission mission : missions) {
            if(now > mission.time + TimeUtils.HOUR_DURATION * hours) {
                MissionManager.getInstance().removeMission(mission);
            }
        }
    }

}
