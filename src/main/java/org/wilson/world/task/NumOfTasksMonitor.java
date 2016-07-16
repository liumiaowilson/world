package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Task;
import org.wilson.world.monitor.MonitorParticipant;

public class NumOfTasksMonitor implements MonitorParticipant {
    private int limit;
    
    public NumOfTasksMonitor(int limit) {
        this.limit = limit;
    }
    
    @Override
    public String getName() {
        return "Too Many Tasks";
    }

    @Override
    public boolean doMonitor() {
        List<Task> ideas = TaskManager.getInstance().getTasks();
        if(ideas.size() <= limit) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String getAlertMessage() {
        return "There are too many tasks. Please process them as soon as possible.";
    }
}
