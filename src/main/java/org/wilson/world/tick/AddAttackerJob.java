package org.wilson.world.tick;

import org.apache.log4j.Logger;
import org.wilson.world.manager.NPCManager;
import org.wilson.world.schedule.DefaultJob;

public class AddAttackerJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(AddAttackerJob.class);
    
    @Override
    public void execute() {
        logger.info("Added a new attacker as NPC");
        
        Attacker attacker = NPCManager.getInstance().genNPC();
        NPCManager.getInstance().addNPC(attacker);
    }

}
