package org.wilson.world.character;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;

public class CoinMissionRewardGenerator implements MissionRewardGenerator {

    @Override
    public MissionReward generate() {
        int max_coins = ConfigManager.getInstance().getConfigAsInt("coin.mission.reward.max", 50);
        int coins = DiceManager.getInstance().random(max_coins);
        
        return new CoinMissionReward(coins);
    }

}
