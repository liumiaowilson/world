package org.wilson.world.sleep;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserItemDataManager;
import org.wilson.world.model.Sleep;
import org.wilson.world.useritem.UserItem;
import org.wilson.world.util.TimeUtils;

public class SleepRewardEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        if(EventType.CreateSleep == event.type) {
            Sleep sleep = (Sleep) event.data.get("data");
            if(sleep != null) {
                long duration = sleep.endTime - sleep.startTime;
                int hours = ConfigManager.getInstance().getConfigAsInt("sleep.best.duration.hours", 8);
                if(duration >= hours * TimeUtils.HOUR_DURATION) {
                    if(DiceManager.getInstance().dice(50)) {
                        UserItem item = UserItemDataManager.getInstance().randomUserItem();
                        InventoryItemManager.getInstance().addUserItem(item);
                        
                        NotifyManager.getInstance().notifySuccess("Gained [" + item.getName() + "] from having a good sleep.");
                    }
                }
            }
        }
    }

}
