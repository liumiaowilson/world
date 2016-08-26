package org.wilson.world.exp;

import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.mission.MissionReward;

public class ExpMissionReward implements MissionReward {
    private int amount;
    
    public ExpMissionReward(int amount) {
        this.amount = amount;
    }
    
    @Override
    public String getName() {
        return "Exp [" + this.amount + "]";
    }

    @Override
    public void deliver() {
        int exp = ExpManager.getInstance().getExp();
        exp += this.amount;
        ExpManager.getInstance().setExp(exp);
        
        NotifyManager.getInstance().notifySuccess("Gained [" + this.amount + "] exp from finishing mission");
    }

    @Override
    public int getWorth() {
        return this.amount;
    }

}
