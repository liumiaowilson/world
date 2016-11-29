package org.wilson.world.reward;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.RewardManager;
import org.wilson.world.schedule.DefaultJob;

public class ShowUpJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(ShowUpJob.class);
    
    @Override
    public void execute() {
        logger.info("Refill show-up rewards");
        
        int max = ConfigManager.getInstance().getConfigAsInt("show_up.reward.max", 20);
        int n = DiceManager.getInstance().random(max);
        if(n < 1) {
        	n = 1;
        }
        
        Reward reward = new Reward();
        reward.amount = n;
        RewardType [] types = RewardType.values();
        int p = DiceManager.getInstance().random(types.length);
        reward.type = types[p];
        RewardManager.getInstance().setShowUpReward(reward);
    }

}
