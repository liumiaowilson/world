package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;

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
        
        int old_hp = CharManager.getInstance().getHP();
        int max_hp = CharManager.getInstance().getMaxHP();
        int hp = old_hp + amount;
        if(hp > max_hp) {
            hp = max_hp;
        }
        CharManager.getInstance().setHP(hp);
        
        int delta = hp - old_hp;
        NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] hp.");
    }

}
