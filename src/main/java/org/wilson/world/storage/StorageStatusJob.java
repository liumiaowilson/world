package org.wilson.world.storage;

import org.apache.log4j.Logger;
import org.wilson.world.manager.StorageManager;
import org.wilson.world.model.Storage;
import org.wilson.world.web.SystemWebJob;

public class StorageStatusJob extends SystemWebJob {
	private static final Logger logger = Logger.getLogger(StorageStatusJob.class);
	
    @Override
    public void run() throws Exception {
    	logger.info("Update storage status");
    	
    	for(Storage storage : StorageManager.getInstance().getStorages()) {
    		int [] data = StorageManager.getInstance().getStorageUsage(storage);
    		if(data != null && data.length >= 2 && data[1] != 0) {
    			double used_pct = data[0] * 1.0 / data[1];
    			StorageStatus status = StorageManager.getInstance().toStorageStatus(used_pct);
    			StorageManager.getInstance().setStorageStatus(storage, status);
    		}
    	}
    }

}
