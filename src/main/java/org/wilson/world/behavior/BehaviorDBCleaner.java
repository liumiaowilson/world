package org.wilson.world.behavior;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.BehaviorManager;

public class BehaviorDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from behaviors where def_id >= 0 and def_id not in (select id from behavior_defs);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = BehaviorManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
