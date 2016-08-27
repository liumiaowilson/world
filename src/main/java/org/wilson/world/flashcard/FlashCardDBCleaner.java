package org.wilson.world.flashcard;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.FlashCardManager;

public class FlashCardDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from flashcards where set_id >= 0 and set_id not in (select id from flashcard_sets);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = FlashCardManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
