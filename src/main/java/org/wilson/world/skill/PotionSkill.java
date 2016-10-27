package org.wilson.world.skill;

import java.util.Map;

public class PotionSkill extends CommonSkill {
    public PotionSkill() {
        this.setName("Potion Bonus");
        this.setDescription("Improve the effect of using potion");
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
        return SkillTriggerEvent.UsePotion == event;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        UsePotionData data = this.getUsePotionData(args);
        int amount = data.amount * (100 + 10 * skillLevel) / 100;
        data.amount = amount;
    }

}
