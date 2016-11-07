package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.tick.Attacker;

public class BalanceSkill extends CommonSkill {
    public BalanceSkill() {
        this.setName("Balance");
        this.setDescription("Balance HP and MP");
        this.setType(SkillType.Active.name());
        this.setScope(SkillScope.RecoverHP.name());
        this.setTarget(SkillTarget.Self.name());
        this.setCost(10);
        this.setCooldown(10);
    }
    
    @Override
    public boolean canTrigger(Map<String, Object> args) {
        if(!this.isInFight(args)) {
            return true;
        }
        
        Attacker target = this.getSkillSelf(args);
        if(target.getHp() < target.getMaxHp() / 2 && target.getMp() > target.getMaxMp() / 2) {
            return true;
        }
        if(target.getMp() < target.getMaxMp() / 2 && target.getHp() > target.getMaxHp() / 2) {
        	return true;
        }
        return false;
    }

    @Override
    public void trigger(Map<String, Object> args) {
        int skillLevel = this.getSkillLevel(args);
        Attacker target = this.getSkillSelf(args);
        int hp = target.getHp();
        int mp = target.getMp();
        if(hp > mp) {
        	int delta = (hp - mp) / 2;
        	int amount = DiceManager.getInstance().roll(delta, 0.8, 1.2, skillLevel);
        	hp -= delta;
        	mp += amount;
        	int max_mp = target.getMaxMp();
        	if(mp > max_mp) {
        		mp = max_mp;
        	}
        	target.setHp(hp);
        	target.setMp(mp);
        	this.sendMessage(args, "Balanced HP by [-" + delta + "] and MP by [+" + amount + "]");
        }
        else {
        	int delta = (mp - hp) / 2;
        	int amount = DiceManager.getInstance().roll(delta, 0.8, 1.2, skillLevel);
        	mp -= delta;
        	hp += amount;
        	int max_hp = target.getMaxHp();
        	if(hp > max_hp) {
        		hp = max_hp;
        	}
        	target.setHp(hp);
        	target.setMp(mp);
        	this.sendMessage(args, "Balanced HP by [+" + amount + "] and MP by [-" + delta + "]");
        }
    }

}
