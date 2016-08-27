package org.wilson.world.skill;

import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.AbstractDBCleaner;
import org.wilson.world.manager.UserSkillManager;

public class UserSkillDBCleaner extends AbstractDBCleaner {

    @Override
    public String getSQL() {
        return "delete from user_skills where skill_id >= 0 and skill_id not in (select id from skill_data);";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void notifyAfterClean() {
        DAO dao = UserSkillManager.getInstance().getDAO();
        if(dao instanceof CachedDAO) {
            ((CachedDAO)dao).getCache().load();
        }
    }

}
