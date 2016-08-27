package org.wilson.world.hopper;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.HopperDataManager;

public class HopperDataDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from hopper_data where hopper_id >= 0 and hopper_id not in (select id from hoppers);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = HopperDataManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
