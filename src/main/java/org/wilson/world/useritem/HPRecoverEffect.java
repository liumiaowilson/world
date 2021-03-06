package org.wilson.world.useritem;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserSkillManager;

public class HPRecoverEffect implements UserItemEffect {
    private int amount;
    
    public HPRecoverEffect(int amount) {
        this.amount = amount;
    }
    
    @Override
    public void takeEffect() {
        int old_hp = CharManager.getInstance().getHP();
        int max_hp = CharManager.getInstance().getMaxHP();
        int amount = this.amount;
        amount = UserSkillManager.getInstance().usePotionSkill(amount);
        int hp = old_hp + amount;
        if(hp > max_hp) {
            hp = max_hp;
        }
        CharManager.getInstance().setHP(hp);
        
        int delta = hp - old_hp;
        NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] HP");
    }

}
