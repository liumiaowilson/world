package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;
import org.wilson.world.tick.TickMonitor;

public class FleeSkill extends CommonSkill {
    public FleeSkill() {
        this.setName("Flee");
        this.setDescription("Flee away from fights");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.Escape.name());
        this.setTarget(SkillTarget.None.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(!this.isInFight(args)) {
            return false;
        }
        Attacker self = this.getSkillSelf(args);
        int hp = self.getHp();
        int max_hp = self.getMaxHp();
        double ratio = hp * 1.0 / max_hp;
        if(ratio < 0.3) {
            return true;
        }
        return false;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        Attacker user = this.getSkillSelf(args);
        int level = this.getSkillLevel(args);
        
        int amount = 10;
        amount = DiceManager.getInstance().roll(amount, 0.8, 1.2, level);
        int hp = user.getHp();
        hp += amount;
        if(hp > user.getMaxHp()) {
            hp = user.getMaxHp();
        }
        user.setHp(hp);
        
        TickMonitor monitor = this.getSkillMonitor(args);
        monitor.send(message(args, "Successfully fleed away!"));
        monitor.setEnded(true);
    }

}
