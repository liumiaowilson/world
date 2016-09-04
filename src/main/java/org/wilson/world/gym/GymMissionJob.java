package org.wilson.world.gym;

import org.apache.log4j.Logger;
import org.wilson.world.manager.GymManager;
import org.wilson.world.manager.MissionManager;
import org.wilson.world.mission.Mission;
import org.wilson.world.schedule.DefaultJob;

public class GymMissionJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(GymMissionJob.class);
    
    @Override
    public void execute() {
        logger.info("Added a new gym mission");
        
        Mission mission = GymManager.getInstance().generateGymMission();
        if(mission != null) {
            MissionManager.getInstance().addMission(mission);
        }
    }

}
