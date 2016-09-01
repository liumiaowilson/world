package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.QueryManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.Task;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.query.QueryProcessor;

public class ReviewTaskMonitor implements MonitorParticipant {
    private Alert alert = null;
    
    public ReviewTaskMonitor() {
        alert = new Alert();
        alert.id = "Tasks waiting for review found";
        alert.message = "Please review these tasks.";
        alert.canAck = true;
    }
    
    @Override
    public boolean isOK() {
        List<Task> tasks = TaskManager.getInstance().getTasksToReview();
        return tasks.isEmpty();
    }

    @Override
    public Alert getAlert() {
        QueryProcessor processor = QueryManager.getInstance().getQueryProcessor(ReviewTaskQueryProcessor.NAME);
        String url = "query_execute.jsp?id=" + processor.getID();
        alert.url = url;
        
        return alert;
    }

}
