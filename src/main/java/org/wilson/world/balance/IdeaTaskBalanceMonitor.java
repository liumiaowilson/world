package org.wilson.world.balance;

import org.wilson.world.manager.BalanceManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;

public class IdeaTaskBalanceMonitor implements MonitorParticipant {
    private Alert alert;
    
    public IdeaTaskBalanceMonitor() {
        this.alert = new Alert();
        this.alert.id = "Idea-Task Imbalance";
        this.alert.message = "Too many ideas compared with tasks. Please go and process some.";
        this.alert.canAck = true;
        this.alert.url = "idea_list.jsp";
    }
    
    @Override
    public boolean isOK() {
        return BalanceManager.getInstance().getIdeaTaskBalance() <= 0.8;
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }

}
