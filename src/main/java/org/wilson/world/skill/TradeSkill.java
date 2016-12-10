package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.NotifyManager;

public class TradeSkill extends CommonSkill {
    public TradeSkill() {
        this.setName("Trade Bonus");
        this.setDescription("Improve the effect of trading");
        this.setType(SkillType.Passive.name());
        this.setScope(SkillScope.Other.name());
        this.setTarget(SkillTarget.Self.name());
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(this.isInFight(args)) {
            return false;
        }
        
        SkillTriggerEvent event = this.getSkillTriggerEvent(args);
        return SkillTriggerEvent.BuyItem == event || SkillTriggerEvent.SellItem == event;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        TradeData data = this.getTradeData(args);
        int range = data.price * skillLevel / 10;
        if(range <= 0) {
        	range = 1;
        }
        int feedback = DiceManager.getInstance().random(range);
        if(feedback <= 0) {
        	feedback = 1;
        }
        int coins = CharManager.getInstance().getCoins();
        coins += feedback;
        CharManager.getInstance().setCoins(coins);
        
        NotifyManager.getInstance().notifySuccess("Gained [" + feedback + "] from trading");
    }

}
