package org.wilson.world.checklist;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.ChecklistManager;

public class ChecklistDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from checklists where def_id >= 0 and def_id not in (select id from checklist_defs);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = ChecklistManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
