package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.QueryItem;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.query.SystemQueryProcessor;

public class SmallTaskQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Small Task";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query(Map<String, String> args) {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        for(Task task : TaskManager.getInstance().getIndividualTasks()) {
            TaskAttr attr = TaskManager.getInstance().getTaskAttr(task, TaskAttrDefManager.DEF_EFFORT);
            if(attr != null) {
                String value = attr.value;
                if(!StringUtils.isBlank(value)) {
                    try {
                        int amount = Integer.parseInt(value);
                        if(amount <= 2) {
                            QueryItem item = new QueryItem();
                            item.id = task.id;
                            item.name = task.name;
                            item.description = task.content;
                            item.type = TaskManager.getInstance().getItemTypeName();
                            ret.add(item);
                        }
                    }
                    catch(Exception e) {
                    }
                }
            }
        }
        
        return ret;
    }

    @Override
    public boolean isQuickLink() {
        return true;
    }
}
