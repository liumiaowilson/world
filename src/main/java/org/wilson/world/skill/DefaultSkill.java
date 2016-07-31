package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.model.SkillData;

public class DefaultSkill implements Skill {
    private SkillData data;
    private SkillCanTrigger canTrigger;
    private SkillTrigger trigger;
    
    public DefaultSkill(SkillData data, SkillCanTrigger canTrigger, SkillTrigger trigger) {
        this.data = data;
        this.canTrigger = canTrigger;
        this.trigger = trigger;
    }
    
    @Override
    public void setId(int id) {
    }

    @Override
    public int getId() {
        return this.data.id;
    }

    @Override
    public String getName() {
        return this.data.name;
    }

    @Override
    public String getDescription() {
        return this.data.description;
    }

    @Override
    public String getType() {
        return this.data.type;
    }

    @Override
    public String getScope() {
        return this.data.scope;
    }

    @Override
    public String getTarget() {
        return this.data.target;
    }

    @Override
    public int getCost() {
        return this.data.cost;
    }

    @Override
    public int getCooldown() {
        return this.data.cooldown;
    }

    @Override
    public boolean canTrigger(Map<String, Object> args) {
        return this.canTrigger.canTrigger(args);
    }

    @Override
    public void trigger(Map<String, Object> args) {
        this.trigger.trigger(args);
    }

}
