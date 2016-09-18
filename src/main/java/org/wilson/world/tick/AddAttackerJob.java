package org.wilson.world.tick;

import org.apache.log4j.Logger;
import org.wilson.world.manager.NPCManager;
import org.wilson.world.schedule.DefaultJob;

public class AddAttackerJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(AddAttackerJob.class);
    
    @Override
    public void execute() {
        logger.info("Added new attackers as NPC");
        
        NPCManager.getInstance().fillNPCs();
    }

}
