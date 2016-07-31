package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.tick.Attacker;

public class CommonSkill extends SystemSkill {
    private String name;
    private String description;
    private String type;
    private String scope;
    private String target;
    private int cost;
    private int cooldown;
    private SkillCanTrigger canTrigger;
    private SkillTrigger trigger;
    
    public SkillCanTrigger getCanTrigger() {
        return canTrigger;
    }

    public void setCanTrigger(SkillCanTrigger canTrigger) {
        this.canTrigger = canTrigger;
    }

    public SkillTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(SkillTrigger trigger) {
        this.trigger = trigger;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public boolean canTrigger(Map<String, Object> args) {
        return this.canTrigger.canTrigger(args);
    }

    @Override
    public void trigger(Map<String, Object> args) {
        this.trigger.trigger(args);
    }

    public int getSkillLevel(Map<String, Object> args) {
        Integer ret = (Integer) args.get("skill_level");
        if(ret == null) {
            return 1;
        }
        else {
            return ret;
        }
    }
    
    public Attacker getSkillTarget(Map<String, Object> args) {
        Attacker target = (Attacker) args.get("skill_target");
        return target;
    }
}
