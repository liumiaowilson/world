package org.wilson.world.balance;

import org.wilson.world.manager.BalanceManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.useritem.UserItemEffect;

public class BalanceResetEffect implements UserItemEffect {
    private int amount;
    
    public BalanceResetEffect(int amount) {
        this.amount = amount;
    }
    
    @Override
    public void takeEffect() {
        BalanceManager.getInstance().recoverTrainBalance(amount);
        BalanceManager.getInstance().recoverEnergyBalance(amount);
        
        NotifyManager.getInstance().notifySuccess("Balance has recovered by [" + this.amount + "]");
    }

}
