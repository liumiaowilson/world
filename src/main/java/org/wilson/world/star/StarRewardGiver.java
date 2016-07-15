package org.wilson.world.star;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.StarManager;
import org.wilson.world.reward.RewardGiver;

public class StarRewardGiver implements RewardGiver {

    @Override
    public void giveReward(Event event) {
        if(event.type == EventType.StarComplete) {
            Object target = event.data.get("data");
            StarProvider provider = StarManager.getInstance().getStarProvider(target);
            if(provider != null) {
                String name = provider.getDisplayName(target);
                int exp = ExpManager.getInstance().getExp();
                exp = exp + 1;
                ExpManager.getInstance().setExp(exp);
                
                NotifyManager.getInstance().notifySuccess("Gained one extra experience point from starred [" + name + "]!");
            }
        }
    }

}
