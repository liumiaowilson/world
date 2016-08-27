package org.wilson.world.useritem;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.InventoryItemManager;

public class InventoryItemDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from inv_items where item_id >= 0 and item_id not in (select id from user_item_data);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = InventoryItemManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
