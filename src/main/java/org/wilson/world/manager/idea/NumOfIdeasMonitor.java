package org.wilson.world.manager.idea;

import java.util.List;

import org.wilson.world.manager.IdeaManager;
import org.wilson.world.model.Idea;
import org.wilson.world.monitor.MonitorParticipant;

public class NumOfIdeasMonitor implements MonitorParticipant {
    private int limit;
    
    public NumOfIdeasMonitor(int limit) {
        this.limit = limit;
    }
    
    @Override
    public String getName() {
        return "Too Many Ideas";
    }

    @Override
    public boolean doMonitor() {
        List<Idea> ideas = IdeaManager.getInstance().getIdeas();
        if(ideas.size() <= limit) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getAlertMessage() {
        return "There are too many ideas. Please process them as soon as possible.";
    }
}
