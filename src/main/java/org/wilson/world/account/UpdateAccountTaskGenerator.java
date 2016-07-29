package org.wilson.world.account;

import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.SystemTaskGenerator;
import org.wilson.world.task.TaskTemplate;

public class UpdateAccountTaskGenerator extends SystemTaskGenerator {
    public static final String NAME = "UpdateAccount";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task spawn() {
        Task task = new Task();
        task.name = "Update accounts";
        task.content = "Query the amount of accounts and update them.";
        task.createdTime = System.currentTimeMillis();
        task.modifiedTime = task.createdTime;
        
        TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE);
        if(template != null) {
            List<TaskAttr> attrs = template.getTemplateAttributes();
            TaskAttr attr = TaskAttr.getTaskAttr(attrs, TaskAttrDefManager.DEF_PRIORITY);
            if(attr != null) {
                attr.value = String.valueOf("80");
            }
            else {
                attr = TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "80");
                attrs.add(attr);
            }
            task.attrs.addAll(attrs);
        }
        
        return task;
    }

}
