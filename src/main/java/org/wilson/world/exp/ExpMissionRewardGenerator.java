package org.wilson.world.exp;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;

public class ExpMissionRewardGenerator implements MissionRewardGenerator {

    @Override
    public MissionReward generate() {
        int max_exp = ConfigManager.getInstance().getConfigAsInt("exp.mission.reward.max", 50);
        int exp = DiceManager.getInstance().random(max_exp);
        
        return new ExpMissionReward(exp);
    }

}
