package org.wilson.world.humor;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.HumorManager;

public class HumorDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from humors where pattern_id >= 0 and pattern_id not in (select id from humor_patterns);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = HumorManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
