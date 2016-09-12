package org.wilson.world.dao;

import java.util.List;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.manager.DAOManager;
import org.wilson.world.schedule.DefaultJob;

public class DAOUnloadJob extends DefaultJob {

    @SuppressWarnings("rawtypes")
    @Override
    public void execute() {
        List<CachedDAO> cachedDAOs = DAOManager.getInstance().getCachedDAOs();
        for(CachedDAO dao : cachedDAOs) {
            dao.unloadAll();
        }
    }

}
