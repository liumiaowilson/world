package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.TrainerSkill;
import org.wilson.world.model.UserSkill;
import org.wilson.world.skill.ReloadSkillsJob;
import org.wilson.world.skill.Skill;

public class TrainerSkillManager implements ManagerLifecycle{
    private static final Logger logger = Logger.getLogger(TrainerSkillManager.class);
    
    private static TrainerSkillManager instance;
    
    private Cache<Integer, TrainerSkill> skills = null;
    
    private TrainerSkillManager() {
        this.skills = new DefaultCache<Integer, TrainerSkill>("trainer_skill_manager_skills", false);
        
        ScheduleManager.getInstance().addJob(new ReloadSkillsJob());
    }
    
    public static TrainerSkillManager getInstance() {
        if(instance == null) {
            instance = new TrainerSkillManager();
        }
        return instance;
    }
    
    public void reloadSkills() {
        this.skills.clear();
        
        List<Skill> skills = SkillDataManager.getInstance().getSkills();
        List<Skill> load = DiceManager.getInstance().random(skills);
        for(Skill skill : load) {
            TrainerSkill ts = new TrainerSkill();
            ts.id = skill.getId();
            ts.name = skill.getName();
            ts.description = skill.getDescription();
            ts.type = skill.getType();
            ts.scope = skill.getScope();
            ts.target = skill.getTarget();
            ts.cost = skill.getCost();
            ts.cooldown = skill.getCooldown();
            ts.price = 10;
            ts.level = 1;
            this.skills.put(ts.id, ts);
        }
    }

    @Override
    public void start() {
        logger.info("Start to load skills");
        this.reloadSkills();
    }

    @Override
    public void shutdown() {
    }
    
    public List<TrainerSkill> getSkills() {
        return this.skills.getAll();
    }
    
    public List<TrainerSkill> getSkillsToLearn() {
        List<TrainerSkill> ret = new ArrayList<TrainerSkill>();
        
        for(TrainerSkill skill : this.getSkills()) {
            UserSkill us = UserSkillManager.getInstance().getUserSkillsBySkillId(skill.id);
            if(us == null) {
                ret.add(skill);
            }
        }
        
        return ret;
    }
    
    public List<TrainerSkill> getSkillsToUpgrade() {
        List<TrainerSkill> ret = new ArrayList<TrainerSkill>();
        
        for(TrainerSkill skill : this.getSkills()) {
            UserSkill us = UserSkillManager.getInstance().getUserSkillsBySkillId(skill.id);
            if(us != null) {
                if(us.exp == 100) {
                    skill.level = us.level;
                    skill.price = skill.level * 10;
                    ret.add(skill);
                }
            }
        }
        
        return ret;
    }
    
    public TrainerSkill getTrainerSkill(int id) {
        return this.skills.get(id);
    }
    
    public String learn(int id, int price) {
        int points = CharManager.getInstance().getSkillPoints();
        if(points < price) {
            return "User does not have enough skill points.";
        }
        
        TrainerSkill ts = this.getTrainerSkill(id);
        if(ts == null) {
            return "No trainer skill found.";
        }
        
        Skill skill = SkillDataManager.getInstance().getSkill(ts.id);
        if(skill == null) {
            return "No such skill found.";
        }
        
        UserSkill us = UserSkillManager.getInstance().getUserSkillsBySkillId(skill.getId());
        if(us != null) {
            return "User has already learnt the skill.";
        }
        
        points -= price;
        CharManager.getInstance().setSkillPoints(points);
        
        us = new UserSkill();
        us.skillId = skill.getId();
        us.level = 1;
        us.exp = 0;
        us.lastTime = -1;
        UserSkillManager.getInstance().createUserSkill(us);
        
        return null;
    }
    
    public String upgrade(int id, int price) {
        int points = CharManager.getInstance().getSkillPoints();
        if(points < price) {
            return "User does not have enough skill points.";
        }
        
        TrainerSkill ts = this.getTrainerSkill(id);
        if(ts == null) {
            return "No trainer skill found.";
        }
        
        Skill skill = SkillDataManager.getInstance().getSkill(ts.id);
        if(skill == null) {
            return "No such skill found.";
        }
        
        UserSkill us = UserSkillManager.getInstance().getUserSkillsBySkillId(skill.getId());
        if(us == null) {
            return "User has not learnt the skill yet.";
        }
        
        points -= price;
        CharManager.getInstance().setSkillPoints(points);
        
        us.level += 1;
        us.exp = 0;
        UserSkillManager.getInstance().updateUserSkill(us);
        
        return null;
    }
}
