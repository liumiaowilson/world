package org.wilson.world.strategy;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.manager.UserItemDataManager;
import org.wilson.world.useritem.UserItem;

public class TripleThinkingEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        if(EventType.TripleThinking == event.type) {
            Integer size = (Integer) event.data.get("size");
            if(size == null) {
                size = 0;
            }
            
            if(DiceManager.getInstance().dice(size)) {
                UserItem item = UserItemDataManager.getInstance().randomUserItem();
                InventoryItemManager.getInstance().addUserItem(item);
                
                NotifyManager.getInstance().notifySuccess("Gained item [" + item.getName() + "] from triple thinking");
            }
        }
    }

}
