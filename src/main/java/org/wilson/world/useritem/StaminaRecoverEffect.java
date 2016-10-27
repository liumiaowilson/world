package org.wilson.world.useritem;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserSkillManager;

public class StaminaRecoverEffect implements UserItemEffect {
    private int amount;
    
    public StaminaRecoverEffect(int amount) {
        this.amount = amount;
    }
    
    @Override
    public void takeEffect() {
        int old_stamina = CharManager.getInstance().getStamina();
        int max_stamina = CharManager.getInstance().getMaxStamina();
        int amount = this.amount;
        amount = UserSkillManager.getInstance().usePotionSkill(amount);
        int stamina = old_stamina + amount;
        if(stamina > max_stamina) {
            stamina = max_stamina;
        }
        CharManager.getInstance().setStamina(stamina);
        
        int delta = stamina - old_stamina;
        NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] stamina");
    }

}
