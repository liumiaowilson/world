package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.TickMonitor;

public class DamageMPSkill extends CommonSkill {
    private double ratio;
    
    public DamageMPSkill(double ratio) {
        this.ratio = ratio;
        
        this.setName("Burn");
        this.setDescription("Burn the mana of your enemy");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.Attack.name());
        this.setTarget(SkillTarget.Enemy.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(this.isInFight(args)) {
            return true;
        }
        
        return false;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        Attacker user = this.getSkillSelf(args);
        Attacker target = this.getSkillTarget(args);
        TickMonitor monitor = this.getSkillMonitor(args);
        
        int damage = user.getMagicalDamage();
        damage = DiceManager.getInstance().roll(damage, ratio - 0.1, ratio + 0.1, skillLevel);
        if(target != null) {
            int d = user.burnMana(target, damage);
            monitor.send(message(args, "Caused [" + d + "] out of [" + damage + "] magical damage to his/her mana."));
        }
    }

}
