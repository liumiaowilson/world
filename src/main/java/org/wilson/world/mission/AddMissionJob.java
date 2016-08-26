package org.wilson.world.mission;

import org.apache.log4j.Logger;
import org.wilson.world.manager.MissionManager;
import org.wilson.world.schedule.DefaultJob;

public class AddMissionJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(AddMissionJob.class);
    
    @Override
    public void execute() {
        logger.info("Added a new mission");
        
        Mission mission = MissionManager.getInstance().generateMission();
        if(mission != null) {
            MissionManager.getInstance().addMission(mission);
        }
    }

}
