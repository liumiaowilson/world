package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;

public class RefreshSkill extends CommonSkill {
    private int amount;
    
    public RefreshSkill(int amount) {
        this.amount = amount;
        
        this.setName("Refresh");
        this.setDescription("Recover a certain amount of MP");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.RecoverMP.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(!this.isInFight(args)) {
            return true;
        }
        
        Attacker target = this.getSkillSelf(args);
        int lost_mp = target.getMaxMp() - target.getMp();
        if(lost_mp >= this.amount) {
            return true;
        }
        return false;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        int amount = DiceManager.getInstance().roll(this.amount, 0.8, 1.2, skillLevel);
        Attacker target = this.getSkillSelf(args);
        if(target != null) {
            int old_mp = target.getMp();
            int max_mp = target.getMaxMp();
            int mp = old_mp + amount;
            if(mp > max_mp) {
                mp = max_mp;
            }
            target.setMp(mp);
        }
    }

}
