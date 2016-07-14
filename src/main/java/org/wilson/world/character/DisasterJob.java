package org.wilson.world.character;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.schedule.DefaultJob;

public class DisasterJob extends DefaultJob {

    @Override
    public void execute() {
        Disaster disaster = ExtManager.getInstance().getExtension(Disaster.class);
        int level = ExpManager.getInstance().getLevel();
        int max_hp = CharManager.getInstance().getMaxHP();
        int hp = CharManager.getInstance().getHP();
        int damage = disaster.getDamage(level, max_hp, hp);
        if(damage >= hp) {
            //dead
            DeathExecution de = ExtManager.getInstance().getExtension(DeathExecution.class);
            de.receiveDeathPenalty(damage);
            
            CharManager.getInstance().restore();
        }
        else {
            hp = hp - damage;
            CharManager.getInstance().setHP(hp);
        }
    }

}
