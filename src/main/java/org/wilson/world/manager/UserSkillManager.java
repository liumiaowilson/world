package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.UserSkill;
import org.wilson.world.skill.Skill;

public class UserSkillManager implements ItemTypeProvider {
    public static final String NAME = "user_skill";
    
    private static UserSkillManager instance;
    
    private DAO<UserSkill> dao = null;
    
    @SuppressWarnings("unchecked")
    private UserSkillManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(UserSkill.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static UserSkillManager getInstance() {
        if(instance == null) {
            instance = new UserSkillManager();
        }
        return instance;
    }
    
    public void createUserSkill(UserSkill skill) {
        this.dao.create(skill);
    }
    
    public UserSkill getUserSkill(int id) {
        UserSkill skill = this.dao.get(id);
        if(skill != null) {
            this.loadUserSkill(skill);
            return skill;
        }
        else {
            return null;
        }
    }
    
    public List<UserSkill> getUserSkills() {
        List<UserSkill> result = new ArrayList<UserSkill>();
        for(UserSkill skill : this.dao.getAll()) {
            this.loadUserSkill(skill);
            result.add(skill);
        }
        return result;
    }
    
    public void updateUserSkill(UserSkill skill) {
        this.dao.update(skill);
    }
    
    public void deleteUserSkill(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof UserSkill;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        UserSkill skill = (UserSkill)target;
        return String.valueOf(skill.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public void loadUserSkill(UserSkill skill) {
        if(skill == null) {
            return;
        }
        Skill s = SkillDataManager.getInstance().getSkill(skill.skillId);
        if(s == null) {
            return;
        }
        skill.name = s.getName();
        skill.description = s.getDescription();
    }
}
