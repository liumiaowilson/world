package org.wilson.world.storage;

import org.wilson.world.manager.StorageManager;
import org.wilson.world.web.SystemWebJob;

public class StorageSyncJob extends SystemWebJob {

    @Override
    public void run() throws Exception {
        StorageManager.getInstance().sync(this.getMonitor());
    }

}
