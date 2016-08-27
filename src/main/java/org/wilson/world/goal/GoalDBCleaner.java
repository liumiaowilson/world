package org.wilson.world.goal;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.GoalManager;

public class GoalDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from goals where def_id >=0 and def_id not in (select id from goal_defs);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = GoalManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
