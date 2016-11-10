package org.wilson.world.plan;

import java.util.Calendar;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.manager.TaskTemplateManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.SystemTaskGenerator;
import org.wilson.world.task.TaskTemplate;
import org.wilson.world.task.TaskType;

public class PlanTaskGenerator extends SystemTaskGenerator {
    public static final String NAME = "Plan";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task spawn() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(this.getTimeZone());
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(Calendar.MONDAY == day) {
            Task task = new Task();
            task.name = "Make plan";
            task.content = "Make a weekly plan.";
            task.createdTime = System.currentTimeMillis();
            task.modifiedTime = task.createdTime;
            
            TaskTemplate template = TaskTemplateManager.getInstance().getTaskTemplate(ContextManager.CONTEXT_LEISURE + "_" + TaskType.Routine.name());
            if(template != null) {
                List<TaskAttr> attrs = template.getTemplateAttributes();
                TaskAttr attr = TaskAttr.getTaskAttr(attrs, TaskAttrDefManager.DEF_PRIORITY);
                if(attr != null) {
                    attr.value = String.valueOf("90");
                }
                else {
                    attr = TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "90");
                    attrs.add(attr);
                }
                task.attrs.addAll(attrs);
                task.attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_MENU, "plan_view"));
            }
            
            return task;
        }
        else {
            return null;
        }
    }
}
