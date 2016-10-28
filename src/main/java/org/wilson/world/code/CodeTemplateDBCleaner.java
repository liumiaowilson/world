package org.wilson.world.code;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.CodeSnippetManager;

public class CodeTemplateDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from code_snippets where template_id not in (select id from code_templates);";
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
