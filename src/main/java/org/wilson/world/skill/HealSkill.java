package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.TickMonitor;

public class HealSkill extends CommonSkill {
    private int amount;
    
    public HealSkill(int amount) {
        this.amount = amount;
        
        this.setName("Heal");
        this.setDescription("Recover a certain amount of HP");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.RecoverHP.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        TickMonitor monitor = this.getSkillMonitor(args);
        if(monitor == null) {
            //not in fight
            return true;
        }
        
        Attacker target = this.getSkillSelf(args);
        int lost_hp = target.getMaxHp() - target.getHp();
        if(lost_hp >= this.amount) {
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
            int old_hp = target.getHp();
            int max_hp = target.getMaxHp();
            int hp = old_hp + amount;
            if(hp > max_hp) {
                hp = max_hp;
            }
            target.setHp(hp);
        }
    }

}
