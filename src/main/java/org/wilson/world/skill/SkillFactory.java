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
    
    public Skill buildHealSkill(String name, String description, int cost, int cooldown, int amount) {
        HealSkill skill = new HealSkill(amount);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
}
