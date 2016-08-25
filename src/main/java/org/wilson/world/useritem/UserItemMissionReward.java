package org.wilson.world.useritem;

import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.mission.MissionReward;

public class UserItemMissionReward implements MissionReward {
    private UserItem item;
    
    public UserItemMissionReward(UserItem item) {
        this.item = item;
    }
    
    @Override
    public String getName() {
        return this.item.getName();
    }

    @Override
    public void deliver() {
        InventoryItemManager.getInstance().addUserItem(this.item);
    }

    @Override
    public int getWorth() {
        return this.item.getValue();
    }

}
