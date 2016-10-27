package org.wilson.world.useritem;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserSkillManager;

public class MPRecoverEffect implements UserItemEffect {
    private int amount;
    
    public MPRecoverEffect(int amount) {
        this.amount = amount;
    }
    
    @Override
    public void takeEffect() {
        int old_mp = CharManager.getInstance().getMP();
        int max_mp = CharManager.getInstance().getMaxMP();
        int amount = this.amount;
        amount = UserSkillManager.getInstance().usePotionSkill(amount);
        int mp = old_mp + amount;
        if(mp > max_mp) {
            mp = max_mp;
        }
        CharManager.getInstance().setMP(mp);
        
        int delta = mp - old_mp;
        NotifyManager.getInstance().notifySuccess("Recovered [" + delta + "] MP");
    }

}
