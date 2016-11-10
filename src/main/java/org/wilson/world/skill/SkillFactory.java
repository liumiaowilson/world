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
        this.skills.add(this.buildRefreshSkill("Mana Burst", "Burst into a gust of mana", 0, 20, 20));
        
        this.skills.add(this.buildRestSkill("Minor Rest", "Recover a minor amount of stamina", 10, 10, 25));
        this.skills.add(this.buildRestSkill("Medium Rest", "Recover a medium amount of stamina", 20, 20, 50));
        this.skills.add(this.buildRestSkill("Major Rest", "Recover a major amount of stamina", 30, 30, 75));
        
        this.skills.add(this.buildPunchSkill("Minor Punch", "Deliver a punch doing minor damage", 10, 8, 1.2));
        this.skills.add(this.buildPunchSkill("Medium Punch", "Deliver a punch doing medium damage", 20, 8, 1.5));
        this.skills.add(this.buildPunchSkill("Major Punch", "Deliver a punch doing major damage", 30, 8, 1.8));
        
        this.skills.add(this.buildBurnSkill("Minor Burn", "Deliver a hit doing minor burning", 10, 8, 1.2));
        this.skills.add(this.buildBurnSkill("Medium Burn", "Deliver a hit doing medium burning", 20, 8, 1.5));
        this.skills.add(this.buildBurnSkill("Major Burn", "Deliver a hit doing major burning", 30, 8, 1.8));
        
        this.skills.add(this.buildPotionSkill("Potion Bonus", "Improve the effect of using potion"));
        
        this.skills.add(this.buildHunterSkill("Hunter Exp", "Recover an amount of stamina based on hunter experience"));
        
        this.skills.add(this.buildBalanceSkill("Balance", "Balance between HP and MP", 0, 24));
        
        this.skills.add(this.buildResetBalanceSkill("Minor Reset Balance", "Reset minor balance using gallery tickets", 25));
        this.skills.add(this.buildResetBalanceSkill("Medium Reset Balance", "Reset medium balance using gallery tickets", 50));
        this.skills.add(this.buildResetBalanceSkill("Major Reset Balance", "Reset major balance using gallery tickets", 75));
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
    
    public Skill buildPunchSkill(String name, String description, int cost, int cooldown, double ratio) {
        DamageHPSkill skill = new DamageHPSkill(ratio);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
    
    public Skill buildBurnSkill(String name, String description, int cost, int cooldown, double ratio) {
        DamageMPSkill skill = new DamageMPSkill(ratio);
        skill.setName(name);
        skill.setDescription(description);
        skill.setCost(cost);
        skill.setCooldown(cooldown);
        
        return skill;
    }
    
    public Skill buildPotionSkill(String name, String description) {
    	PotionSkill skill = new PotionSkill();
    	skill.setName(name);
    	skill.setDescription(description);
    	return skill;
    }
    
    public Skill buildHunterSkill(String name, String description) {
    	HunterSkill skill = new HunterSkill();
    	skill.setName(name);
    	skill.setDescription(description);
    	return skill;
    }
    
    public Skill buildBalanceSkill(String name, String description, int cost, int cooldown) {
    	BalanceSkill skill = new BalanceSkill();
    	skill.setName(name);
    	skill.setDescription(description);
    	skill.setCost(cost);
    	skill.setCooldown(cooldown);
    	return skill;
    }
    
    public Skill buildResetBalanceSkill(String name, String description, int amount) {
    	ResetBalanceSkill skill = new ResetBalanceSkill(amount);
    	skill.setName(name);
    	skill.setDescription(description);
    	return skill;
    }
}
