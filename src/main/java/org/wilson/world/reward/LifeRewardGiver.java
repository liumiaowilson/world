package org.wilson.world.reward;

import org.wilson.world.event.Event;
import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;

public class LifeRewardGiver implements RewardGiver {

    @Override
    public void giveReward(Event event) {
        int old_hp = CharManager.getInstance().getHP();
        int max_hp = CharManager.getInstance().getMaxHP();
        if(DiceManager.getInstance().dice(5)) {
            if(old_hp == max_hp) {
                return;
            }
            int hp = old_hp + 5;
            if(hp > max_hp) {
                hp = max_hp;
            }
            CharManager.getInstance().setHP(hp);
            
            NotifyManager.getInstance().notifySuccess("Recovered " + (hp - old_hp) + " life point(s) from [" + event.type.toString() + "]!");
        }
    }

}
