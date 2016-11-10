package org.wilson.world.character;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;

public class SkillPointMissionRewardGenerator implements MissionRewardGenerator {

    @Override
    public MissionReward generate() {
        int max_skillpoints = ConfigManager.getInstance().getConfigAsInt("skillpoint.mission.reward.max", 50);
        int skillpoints = DiceManager.getInstance().random(max_skillpoints);
        
        return new SkillPointMissionReward(skillpoints);
    }

}
