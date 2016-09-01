package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.MonitorManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.QueryItem;
import org.wilson.world.model.Task;
import org.wilson.world.query.SystemQueryProcessor;

public class ReviewTaskQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Review Task";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query(Map<String, String> args) {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        for(Task task : TaskManager.getInstance().getTasksToReview()) {
            QueryItem item = new QueryItem();
            item.id = task.id;
            item.name = task.name;
            item.description = task.content;
            item.type = TaskManager.getInstance().getItemTypeName();
            ret.add(item);
        }
        
        if(ret.isEmpty()) {
            Alert alert = TaskManager.getInstance().getReviewTaskMonitor().getAlert();
            MonitorManager.getInstance().removeAlert(alert);
        }
        
        return ret;
    }
}
