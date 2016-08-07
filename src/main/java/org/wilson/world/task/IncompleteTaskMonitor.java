package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.QueryManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Task;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.query.QueryProcessor;

public class IncompleteTaskMonitor implements MonitorParticipant {
    private Alert alert = null;
    
    public IncompleteTaskMonitor() {
        alert = new Alert();
        alert.id = "Incomplete tasks found";
        alert.message = "Please complete the tasks by adding missing attributes.";
        alert.canAck = true;
    }
    
    @Override
    public boolean isOK() {
        List<Task> tasks = IncompleteTaskQueryProcessor.getIncompleteTasks();
        return tasks.isEmpty();
    }

    @Override
    public Alert getAlert() {
        QueryProcessor processor = QueryManager.getInstance().getQueryProcessor(IncompleteTaskQueryProcessor.NAME);
        String url = "query_execute.jsp?id=" + processor.getID();
        alert.url = url;
        
        return alert;
    }

}
