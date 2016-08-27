package org.wilson.world.task;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.TaskTagManager;

public class TaskTagDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from task_tags where task_id >= 0 and task_id not in (select id from tasks);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = TaskTagManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
