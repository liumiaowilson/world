package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Task;
import org.wilson.world.monitor.MonitorParticipant;

public class NumOfTasksMonitor implements MonitorParticipant {
    private int limit;
    private Alert alert;
    
    public NumOfTasksMonitor(int limit) {
        this.limit = limit;
        
        this.alert = new Alert();
        this.alert.id = "Too Many Tasks";
        this.alert.message = "There are too many tasks. Please process them as soon as possible.";
        this.alert.url = "task_list.jsp";
    }
    
    @Override
    public boolean isOK() {
        List<Task> ideas = TaskManager.getInstance().getTasks();
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
