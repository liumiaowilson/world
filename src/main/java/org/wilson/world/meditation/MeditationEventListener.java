package org.wilson.world.meditation;

import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.manager.BalanceManager;
import org.wilson.world.manager.NotifyManager;
import org.wilson.world.model.Meditation;
import org.wilson.world.util.TimeUtils;

public class MeditationEventListener implements EventListener {

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        if(EventType.CreateMeditation == event.type) {
            Meditation meditation = (Meditation) event.data.get("data");
            if(meditation != null) {
                int recover = (int) (meditation.duration * 10.0 / TimeUtils.MINUTE_DURATION); 
                BalanceManager.getInstance().recoverTrainBalance(recover);
                BalanceManager.getInstance().recoverEnergyBalance(recover);
                
                NotifyManager.getInstance().notifySuccess("Recovered [" + recover + "] balance");
            }
        }
    }

}
