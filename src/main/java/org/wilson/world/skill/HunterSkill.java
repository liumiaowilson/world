package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;

public class HunterSkill extends CommonSkill {
    public HunterSkill() {
        this.setName("Hunter");
        this.setDescription("Recover a certain amount of stamina based on hunter experience");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.Other.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(this.isInFight(args)) {
            return false;
        }
        
        return true;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        int kills = CharManager.getInstance().getKills();
        int max_stamina = CharManager.getInstance().getMaxStamina();
        int amount = (int) (max_stamina * Math.sqrt(kills) / 100.0);
        amount = DiceManager.getInstance().roll(amount, 0.8, 1.2, skillLevel);
        
        int old_stamina = CharManager.getInstance().getStamina();
        int stamina = old_stamina + amount;
        if(stamina > max_stamina) {
            stamina = max_stamina;
        }
        CharManager.getInstance().setStamina(stamina);
        
        NotifyManager.getInstance().notifySuccess("Recovered [" + (stamina - old_stamina) + "] stamina.");
    }

}
