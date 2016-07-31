package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;

public class HealSkill extends CommonSkill {
    private int amount;
    
    public HealSkill(int amount) {
        this.amount = amount;
        
        this.setName("Heal");
        this.setDescription("Recover a certain amount of HP");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.All.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        return true;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        int amount = DiceManager.getInstance().roll(this.amount, 0.8, 1.2, skillLevel);
        Attacker target = this.getSkillTarget(args);
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
