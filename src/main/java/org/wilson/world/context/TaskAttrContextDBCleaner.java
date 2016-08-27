package org.wilson.world.context;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.TaskAttrManager;

public class TaskAttrContextDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from task_attrs where name = 'Context' and value not in (select id from contexts);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = TaskAttrManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
