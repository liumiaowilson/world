package org.wilson.world.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.IdeaManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.QueryItem;
import org.wilson.world.model.Task;

public class IdeaAndTaskQueryProcessor extends SystemQueryProcessor {
    public static final String NAME = "Idea and task";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<QueryItem> query(Map<String, String> args) {
        List<QueryItem> ret = new ArrayList<QueryItem>();
        
        for(Idea idea : IdeaManager.getInstance().getIdeas()) {
            QueryItem item = new QueryItem();
            item.id = idea.id;
            item.name = idea.name;
            item.description = idea.content;
            item.type = IdeaManager.getInstance().getItemTypeName();
            ret.add(item);
        }
        
        for(Task task : TaskManager.getInstance().getTasks()) {
            QueryItem item = new QueryItem();
            item.id = task.id;
            item.name = task.name;
            item.description = task.content;
            item.type = TaskManager.getInstance().getItemTypeName();
            ret.add(item);
        }
        
        return ret;
    }

    @Override
    public boolean isQuickLink() {
        return true;
    }
}
