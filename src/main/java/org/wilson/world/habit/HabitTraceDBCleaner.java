package org.wilson.world.habit;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.HabitTraceManager;

public class HabitTraceDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from habit_traces where habit_id >= 0 and habit_id not in (select id from habits);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = HabitTraceManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
