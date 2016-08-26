package org.wilson.world.character;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.mission.MissionReward;

public class CoinMissionReward implements MissionReward {
    private int amount;
    
    public CoinMissionReward(int amount) {
        this.amount = amount;
    }
    
    @Override
    public String getName() {
        return "Coins [" + this.amount + "]";
    }

    @Override
    public void deliver() {
        int coins = CharManager.getInstance().getCoins();
        coins += this.amount;
        CharManager.getInstance().setCoins(coins);
        
        NotifyManager.getInstance().notifySuccess("Gained [" + this.amount + "] coins from finishing mission");
    }

    @Override
    public int getWorth() {
        return this.amount;
    }

}
