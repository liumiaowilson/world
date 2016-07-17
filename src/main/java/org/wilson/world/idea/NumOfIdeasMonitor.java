package org.wilson.world.idea;

import java.util.List;

import org.wilson.world.manager.IdeaManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Idea;
import org.wilson.world.monitor.MonitorParticipant;

public class NumOfIdeasMonitor implements MonitorParticipant {
    private int limit;
    private Alert alert;
    
    public NumOfIdeasMonitor(int limit) {
        this.limit = limit;
        
        this.alert = new Alert();
        this.alert.id = "Too Many Ideas";
        this.alert.message = "There are too many ideas. Please process them as soon as possible.";
        this.alert.url = "idea_list.jsp";
    }

    @Override
    public boolean isOK() {
        List<Idea> ideas = IdeaManager.getInstance().getIdeas();
        if(ideas.size() <= limit) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Alert getAlert() {
        return this.alert;
    }
}
