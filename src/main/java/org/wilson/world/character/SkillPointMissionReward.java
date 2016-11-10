package org.wilson.world.character;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.mission.MissionReward;

public class SkillPointMissionReward implements MissionReward {
    private int amount;
    
    public SkillPointMissionReward(int amount) {
        this.amount = amount;
    }
    
    @Override
    public String getName() {
        return "Skill Points [" + this.amount + "]";
    }

    @Override
    public void deliver() {
        int skillpoints = CharManager.getInstance().getSkillPoints();
        skillpoints += this.amount;
        CharManager.getInstance().setSkillPoints(skillpoints);
        
        NotifyManager.getInstance().notifySuccess("Gained [" + this.amount + "] skill points from finishing mission");
    }

    @Override
    public int getWorth() {
        return this.amount;
    }

}
