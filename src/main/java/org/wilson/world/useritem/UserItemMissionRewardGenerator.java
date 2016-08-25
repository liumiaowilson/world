package org.wilson.world.useritem;

import org.wilson.world.manager.UserItemDataManager;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;

public class UserItemMissionRewardGenerator implements MissionRewardGenerator {

    @Override
    public MissionReward generate() {
        UserItem item = UserItemDataManager.getInstance().randomUserItem();
        
        if(item == null) {
            return null;
        }
        
        return new UserItemMissionReward(item);
    }

}
