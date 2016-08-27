package org.wilson.world.balance;

import org.apache.log4j.Logger;
import org.wilson.world.manager.BalanceManager;
import org.wilson.world.schedule.DefaultJob;

public class BalanceResetJob extends DefaultJob {
    private static final Logger logger = Logger.getLogger(BalanceResetJob.class);
    
    @Override
    public void execute() {
        logger.info("Start to recover balance");
        
        BalanceManager.getInstance().recoverTrainBalance(5);
        BalanceManager.getInstance().recoverEnergyBalance(5);
    }

}
