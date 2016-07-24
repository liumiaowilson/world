package org.wilson.world.character;

import java.util.List;

import org.wilson.world.manager.CharManager;
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.StatusEffect;
import org.wilson.world.schedule.DefaultJob;

public class StatusRefreshJob extends DefaultJob {
    
    @Override
    public void execute() {
        List<StatusEffect> effects = CharManager.getInstance().getStatusEffects();
        for(StatusEffect effect : effects) {
            if(!CharManager.getInstance().isValidStatus(effect)) {
                CharManager.getInstance().loseStatus(effect.status);
                
                Alert alert = new Alert();
                alert.id = "Status [" + effect.status.getName() + "] lost";
                alert.message = "Status [" + effect.status.getName() + "] has faded.";
                alert.canAck = true;
                MonitorManager.getInstance().addAlert(alert);
            }
        }
    }

}
