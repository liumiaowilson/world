package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.BalanceManager;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.InventoryItemManager;
import org.wilson.world.manager.NotifyManager;

public class ResetBalanceSkill extends CommonSkill {
    private int amount;
    
    public ResetBalanceSkill(int amount) {
        this.amount = amount;
        
        this.setName("Rest");
        this.setDescription("Recover a certain amount of balance using gallery tickets");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.Other.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(0);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(this.isInFight(args)) {
            return false;
        }
        
        return true;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        int amount = DiceManager.getInstance().roll(this.amount, 0.8, 1.2, skillLevel);
        
        String ret = InventoryItemManager.getInstance().useGalleryTicket(this.amount);
        if(ret == null) {
        	BalanceManager.getInstance().recoverEnergyBalance(amount);
        	BalanceManager.getInstance().recoverTrainBalance(amount);
        	
        	NotifyManager.getInstance().notifySuccess("Recovered [" + amount + "] balance");
        }
        else {
        	NotifyManager.getInstance().notifyDanger(ret);
        }
    }

}
