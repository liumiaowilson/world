package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class RoutineTaskTemplateComponent extends AbstractTaskTemplateComponent {
    
    public RoutineTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TYPE, this.getName()));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "2"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TOPIC, "NA"));
    }
    
    @Override
    public String getName() {
        return TaskType.Routine.name();
    }

}
