package org.wilson.world.reward;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.RewardManager;
import org.wilson.world.schedule.DefaultJob;

public class ShowUpJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(ShowUpJob.class);
    
    @Override
    public void execute() {
        logger.info("Refill show-up rewards");
        
        int max = ConfigManager.getInstance().getConfigAsInt("show_up.reward.max", 20);
        Reward reward = RewardManager.getInstance().generateReward(max);
        RewardManager.getInstance().setShowUpReward(reward);
    }

}
