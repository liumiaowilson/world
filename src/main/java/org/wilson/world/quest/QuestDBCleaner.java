package org.wilson.world.quest;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.QuestManager;

public class QuestDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from quests where def_id >= 0 and def_id not in (select id from quest_defs);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = QuestManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
