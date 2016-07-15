package org.wilson.world.reward;

import org.wilson.world.event.Event;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;

public class ExpRewardGiver implements RewardGiver {

    @Override
    public void giveReward(Event event) {
        int exp = ExpManager.getInstance().getExp();
        if(DiceManager.getInstance().dice(5)) {
            exp = exp + 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifySuccess("Gained one extra experience point from [" + event.type.toString() + "]!");
            return;
        }
        
        if(DiceManager.getInstance().dice(5)) {
            exp = exp - 1;
            ExpManager.getInstance().setExp(exp);
            
            NotifyManager.getInstance().notifyDanger("Lost one experience point from [" + event.type.toString() + "]!");
            return;
        }
    }

}
