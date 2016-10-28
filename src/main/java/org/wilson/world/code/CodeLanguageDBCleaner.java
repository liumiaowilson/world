package org.wilson.world.code;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.CodeSnippetManager;

public class CodeLanguageDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from code_snippets where language_id not in (select id from code_languages);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = CodeSnippetManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
