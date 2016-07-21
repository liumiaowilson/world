package org.wilson.world.reward;

import org.wilson.world.event.Event;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;

public class LifeRewardGiver implements RewardGiver {

    @Override
    public void giveReward(Event event) {
        if(DiceManager.getInstance().dice(5)) {
            int delta = CharManager.getInstance().increaseHP(5);
            if(delta != 0) {

                NotifyManager.getInstance().notifySuccess("Recovered " + delta + " life point(s) from [" + event.type.toString() + "]!");
            }
        }
    }

}
