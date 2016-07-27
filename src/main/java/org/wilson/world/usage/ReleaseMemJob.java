package org.wilson.world.usage;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.schedule.DefaultJob;

public class ReleaseMemJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(ReleaseMemJob.class);
    
    @Override
    public void execute() {
        logger.info("Try to release memory");
        
        ConsoleManager.getInstance().releaseMemory();
    }

}
