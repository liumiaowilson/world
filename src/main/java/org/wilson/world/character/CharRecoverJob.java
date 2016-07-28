package org.wilson.world.character;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.NPCManager;
import org.wilson.world.schedule.DefaultJob;
import org.wilson.world.tick.Attacker;

public class CharRecoverJob extends DefaultJob {

    @Override
    public void execute() {
        int hp = CharManager.getInstance().getHP();
        int max_hp = CharManager.getInstance().getMaxHP();
        if(hp < max_hp) {
            hp = hp + 2;
            if(hp > max_hp) {
                hp = max_hp;
            }
            CharManager.getInstance().setHP(hp);
        }
        
        int mp = CharManager.getInstance().getMP();
        int max_mp = CharManager.getInstance().getMaxMP();
        if(mp < max_mp) {
            mp = mp + 2;
            if(mp > max_mp) {
                mp = max_mp;
            }
            CharManager.getInstance().setMP(mp);
        }
        
        for(Attacker npc : NPCManager.getInstance().getNPCs()) {
            hp = npc.getHp();
            max_hp = npc.getMaxHp();
            if(hp < max_hp) {
                hp = hp + 2;
                if(hp > max_hp) {
                    hp = max_hp;
                }
                npc.setHp(hp);
            }
            
            mp = npc.getMp();
            max_mp = npc.getMaxMp();
            if(mp < max_mp) {
                mp = mp + 2;
                if(mp > max_mp) {
                    mp = max_mp;
                }
                npc.setMp(mp);
            }
        }
    }

}
