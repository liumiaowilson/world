package org.wilson.world.useritem;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;

public class StrengthBonusEffect implements UserItemEffect {
    private int amount;
    
    public StrengthBonusEffect(int amount) {
        this.amount = amount;
    }
    
    @Override
    public void takeEffect() {
        int old_value = CharManager.getInstance().getStrength();
        int value = old_value + this.amount;
        int max_value = CharManager.getInstance().getAttributeLimit();
        if(value > max_value) {
            value = max_value;
        }
        if(value < old_value) {
            value = old_value;
        }
        CharManager.getInstance().setStrength(value);
        
        int delta = value - old_value;
        NotifyManager.getInstance().notifySuccess("Added [" + delta + "] strength");
    }

}
