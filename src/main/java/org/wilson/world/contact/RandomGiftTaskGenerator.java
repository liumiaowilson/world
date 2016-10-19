package org.wilson.world.contact;

import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.SystemTaskGenerator;
import org.wilson.world.task.TaskTemplate;
import org.wilson.world.task.TaskType;

public class RandomGiftTaskGenerator extends SystemTaskGenerator {
    public static final String NAME = "RandomGift";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task spawn() {
        Task task = new Task();
        task.name = "Random Gift";
        task.content = "Prepare a gif and send it to your most loved one.";
        task.createdTime = System.currentTimeMillis();
        task.modifiedTime = task.createdTime;
        
        TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE + "_" + TaskType.Relationship.name());
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
