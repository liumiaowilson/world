package org.wilson.world.character;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.ExpManager;
import org.wilson.world.manager.ExtManager;
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.model.Alert;
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
            
            Alert alert = new Alert();
            alert.id = "You have been ATTACKED!";
            alert.message = "You have received [" + damage + "] damage and lost one life.";
            alert.canAck = true;
            MonitorManager.getInstance().addAlert(alert);
        }
        else {
            hp = hp - damage;
            CharManager.getInstance().setHP(hp);
            
            Alert alert = new Alert();
            alert.id = "You have been ATTACKED!";
            alert.message = "You have received [" + damage + "] damage and your current hp is [" + hp + "].";
            alert.canAck = true;
            MonitorManager.getInstance().addAlert(alert);
        }
    }

}
