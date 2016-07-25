package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.QueryItem;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.query.SystemQueryProcessor;

public class IncompleteTaskQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Incomplete Task";
    
    public static final String [] REQUIRED_ATTR_NAMES = new String [] {TaskAttrDefManager.DEF_CONTEXT, TaskAttrDefManager.DEF_ARTIFACT, TaskAttrDefManager.DEF_PRIORITY, TaskAttrDefManager.DEF_URGENCY};
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query() {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        for(Task task : getIncompleteTasks()) {
            QueryItem item = new QueryItem();
            item.id = task.id;
            item.name = task.name;
            item.description = task.content;
            item.type = TaskManager.getInstance().getItemTypeName();
            ret.add(item);
        }
        
        return ret;
    }
    
    public static List<Task> getIncompleteTasks() {
        List<Task> ret = new ArrayList<Task>();
        
        for(Task task : TaskManager.getInstance().getTasks()) {
            if(isInComplete(task)) {
                ret.add(task);
            }
        }
        
        return ret;
    }
    
    public static boolean isInComplete(Task task) {
        if(task == null) {
            return false;
        }
        
        for(String name : REQUIRED_ATTR_NAMES) {
            TaskAttr attr = TaskManager.getInstance().getTaskAttr(task, name);
            if(attr == null || StringUtils.isBlank(attr.value)) {
                return true;
            }
        }
        
        return false;
    }
}
