package org.wilson.world.tick;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.skill.Skill;
import org.wilson.world.skill.SkillScope;

public class GameSkill {
    private Skill         skill;
    private GameSkillType type;
    private int           level;
    private int           exp;
    private int           stepId;
    
    public GameSkill(Skill skill, int level, int exp) {
        this.skill = skill;
        this.type = this.detectType(skill);
        this.level = level;
        this.exp = exp;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getCooldown() {
        return this.skill.getCooldown();
    }

    public int getId() {
        return this.skill.getId();
    }

    public String getName() {
        return this.skill.getName();
    }

    public GameSkillType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    
    public int getCost() {
        return this.skill.getCost();
    }
    
    private GameSkillType detectType(Skill skill) {
        if(skill == null) {
            return GameSkillType.Idle;
        }
        
        if(SkillScope.Attack.name().equals(skill.getScope())) {
            return GameSkillType.Attack;
        }
        else if(SkillScope.Buf.name().equals(skill.getScope())) {
            return GameSkillType.Buf;
        }
        else if(SkillScope.Debuf.name().equals(skill.getScope())) {
            return GameSkillType.Debuf;
        }
        else if(SkillScope.RecoverHP.name().equals(skill.getScope())) {
            return GameSkillType.RecoverHP;
        }
        else if(SkillScope.RecoverMP.name().equals(skill.getScope())) {
            return GameSkillType.RecoverMP;
        }
        else if(SkillScope.Escape.name().equals(skill.getScope())) {
            return GameSkillType.Escape;
        }
        else {
            return GameSkillType.Idle;
        }
    }
    
    private Map<String, Object> getArgs(Attacker self, Attacker target) {
        Map<String, Object> args = new HashMap<String, Object>();
        
        args.put("skill_level", this.level);
        args.put("skill_self", self);
        args.put("skill_target", target);
        
        return args;
    }
    
    public boolean canTrigger(Attacker self, Attacker target) {
        return this.skill.canTrigger(this.getArgs(self, target));
    }
    
    public void trigger(Attacker self, Attacker target) {
        int mp = self.getMp();
        mp -= this.getCost();
        self.setMp(mp);
        
        this.skill.trigger(this.getArgs(self, target));
        
        this.exp += 1;
    }
}
