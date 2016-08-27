package org.wilson.world.item;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ItemManager;
import org.wilson.world.schedule.DefaultJob;

public class DBCleanJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(DBCleanJob.class);
    
    @Override
    public void execute() {
        logger.info("Start to clean db ...");
        
        ItemManager.getInstance().cleanDB();
    }

}
