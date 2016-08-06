package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.UserSkill;
import org.wilson.world.skill.Skill;
import org.wilson.world.skill.SkillScope;
import org.wilson.world.tick.Attacker;
import org.wilson.world.util.TimeUtils;

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
        ItemManager.getInstance().checkDuplicate(skill);
        
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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
        
        if(skill.lastTime < 0) {
            skill.cd = "";
        }
        else {
            long nextTime = this.getNextAvailableTime(skill, s);
            long now = System.currentTimeMillis();
            if(nextTime <= now) {
                skill.cd = "";
            }
            else {
                String str = TimeUtils.getTimeReadableString(nextTime - now);
                skill.cd = "In " + str;
            }
        }
    }
    
    public long getNextAvailableTime(UserSkill skill, Skill s) {
        if(skill == null) {
            return -1;
        }
        
        if(skill.lastTime < 0) {
            return System.currentTimeMillis();
        }
        
        if(s == null) {
            return -1;
        }
        
        long lastTime = skill.lastTime;
        long nextTime = lastTime + s.getCooldown() * TimeUtils.HOUR_DURATION;
        return nextTime;
    }
    
    public long getNextAvailableTime(UserSkill skill) {
        if(skill == null) {
            return -1;
        }
        
        if(skill.lastTime < 0) {
            return System.currentTimeMillis();
        }
        
        Skill s = SkillDataManager.getInstance().getSkill(skill.skillId);
        if(s == null) {
            return -1;
        }
        return this.getNextAvailableTime(skill, s);
    }
    
    public UserSkill getUserSkillsBySkillId(int skillId) {
        for(UserSkill skill : this.getUserSkills()) {
            if(skill.skillId == skillId) {
                return skill;
            }
        }
        return null;
    }
    
    public String copy() {
        int skillpoints = CharManager.getInstance().getSkillPoints();
        if(skillpoints < 1) {
            return "User does not have enough skill points.";
        }
        skillpoints -= 1;
        CharManager.getInstance().setSkillPoints(skillpoints);
        
        if(DiceManager.getInstance().dice(5)) {
            Skill skill = SkillDataManager.getInstance().randomSkill();
            if(skill == null) {
                return "No skill to copy.";
            }
            
            UserSkill old = this.getUserSkillsBySkillId(skill.getId());
            if(old == null) {
                UserSkill us = new UserSkill();
                us.skillId = skill.getId();
                us.level = 1;
                us.exp = 0;
                us.lastTime = -1;
                this.createUserSkill(us);
            }
            else {
                return "User already knows this skill.";
            }
        }
        else {
            return "Failed to copy a skill.";
        }
        
        return null;
    }
    
    public String use(int id) {
        UserSkill us = this.getUserSkill(id);
        if(us == null) {
            return "No such user skill found.";
        }
        
        Skill skill = SkillDataManager.getInstance().getSkill(us.skillId);
        if(skill == null) {
            return "No skill found.";
        }
        
        long nextTime = this.getNextAvailableTime(us, skill);
        long now = System.currentTimeMillis();
        if(nextTime > now) {
            return "Skill is still in cooldown.";
        }
        
        int cost = skill.getCost();
        int old_mp = CharManager.getInstance().getMP();
        if(cost > old_mp) {
            return "No enough mana to use the skill.";
        }
        
        Map<String, Object> args = new HashMap<String, Object>();
        Attacker user = CharManager.getInstance().getAttacker();
        args.put("skill_level", us.level);
        args.put("skill_self", user);
        args.put("skill_target", null);
        if(skill.canTrigger(args)) {
            int mp = old_mp - cost;
            CharManager.getInstance().setMP(mp);
            user.setMp(mp);
            
            skill.trigger(args);
            
            int hp = CharManager.getInstance().getHP();
            if(user.getHp() != hp) {
                CharManager.getInstance().setHP(user.getHp());
                
                int delta = user.getHp() - hp;
                if(delta > 0) {
                    NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] HP.");
                }
                else {
                    NotifyManager.getInstance().notifyDanger("Lost [" + (-delta) + "] HP."); 
                }
            }
            
            mp = CharManager.getInstance().getMP();
            if(user.getMp() != mp) {
                CharManager.getInstance().setMP(user.getMp());
                
                int delta = user.getMp() - mp;
                if(delta > 0) {
                    NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] MP.");
                }
                else {
                    NotifyManager.getInstance().notifyDanger("Lost [" + (-delta) + "] MP."); 
                }
            }
            
            us.lastTime = now;
            us.exp += 1;
            if(us.exp > 100) {
                us.exp = 100;
            }
            this.updateUserSkill(us);
        }
        else {
            return "Skill cannot be triggered.";
        }
        
        return null;
    }
    
    public boolean isDisabled(UserSkill us) {
        if(us == null) {
            return true;
        }
        
        Skill skill = SkillDataManager.getInstance().getSkill(us.skillId);
        if(skill == null) {
            return true;
        }
        
        if(!SkillScope.RecoverHP.name().equals(skill.getScope()) 
                && !SkillScope.RecoverMP.name().equals(skill.getScope()) 
                && !SkillScope.Buf.name().equals(skill.getScope())) {
            return true;
        }
        
        long nextTime = this.getNextAvailableTime(us);
        return nextTime > System.currentTimeMillis();
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        UserSkill skill = (UserSkill)target;
        return skill.name;
    }
}
