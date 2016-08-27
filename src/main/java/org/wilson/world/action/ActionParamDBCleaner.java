package org.wilson.world.action;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.ActionParamManager;

public class ActionParamDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from action_params where action_id >= 0 and action_id not in (select id from actions);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = ActionParamManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
