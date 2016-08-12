package org.wilson.world.useritem;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserItemDataManager;

public class RandomUserItemEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.GainExperience.equals(event.type)) {
            if(DiceManager.getInstance().dice(1)) {
                UserItem userItem = UserItemDataManager.getInstance().randomUserItem();
                if(userItem != null) {
                    InventoryItemManager.getInstance().addUserItem(userItem);
                    
                    NotifyManager.getInstance().notifySuccess("Collected a random user item [" + userItem.getName() + "].");
                }
            }
        }
    }

}
