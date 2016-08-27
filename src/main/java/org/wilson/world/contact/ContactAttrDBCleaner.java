package org.wilson.world.contact;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.ContactAttrManager;

public class ContactAttrDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from contact_attrs where contact_id not in (select id from contacts);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = ContactAttrManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
