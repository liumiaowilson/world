package org.wilson.world.skill;

import java.util.ArrayList;
import java.util.List;

public class SkillFactory {
    private static SkillFactory instance;
    
    private List<Skill> skills = new ArrayList<Skill>();
    
    private SkillFactory() {
        this.skills.add(this.buildHealSkill("Minor Heal", "Recover a minor amount of HP", 10, 10, 25));
        this.skills.add(this.buildHealSkill("Medium Heal", "Recover a medium amount of HP", 20, 10, 50));
        this.skills.add(this.buildHealSkill("Major Heal", "Recover a major amount of HP", 30, 10, 75));
        
        this.skills.add(this.buildFleeSkill("Flee", "Flee away from the battlefield", 15, 50));
        
        this.skills.add(this.buildRefreshSkill("Minor Refresh", "Recover a minor amount of MP", 10, 10, 25));
        this.skills.add(this.buildRefreshSkill("Medium Refresh", "Recover a medium amount of MP", 20, 10, 50));
        this.skills.add(this.buildRefreshSkill("Major Refresh", "Recover a major amount of MP", 30, 10, 75));
        
        this.skills.add(this.buildRestSkill("Minor Rest", "Recover a minor amount of stamina", 10, 10, 25));
        this.skills.add(this.buildRestSkill("Medium Rest", "Recover a medium amount of stamina", 20, 10, 50));
        this.skills.add(this.buildRestSkill("Major Rest", "Recover a major amount of stamina", 30, 10, 75));
    }
    
    public static SkillFactory getInstance() {
        if(instance == null) {
            instance = new SkillFactory();
        }
        return instance;
    }
    
    public List<Skill> getSkills() {
        return this.skills;
    }
    
    public Skill buildFleeSkill(String name, String description, int cost, int cooldown) {
        FleeSkill skill = new FleeSkill();
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
    
    public Skill buildHealSkill(String name, String description, int cost, int cooldown, int amount) {
        HealSkill skill = new HealSkill(amount);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
    
    public Skill buildRestSkill(String name, String description, int cost, int cooldown, int amount) {
        RestSkill skill = new RestSkill(amount);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
    
    public Skill buildRefreshSkill(String name, String description, int cost, int cooldown, int amount) {
        RefreshSkill skill = new RefreshSkill(amount);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
}
