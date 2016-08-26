package org.wilson.world.item;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ItemManager;
import org.wilson.world.schedule.DefaultJob;

public class DataSizeTrackJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(DataSizeTrackJob.class);
    
    @Override
    public void execute() {
        logger.info("Track data size ...");
        
        ItemManager.getInstance().trackDataSize();
    }

}
