package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class OutdoorTaskTemplateComponent extends AbstractTaskTemplateComponent {
    
    public OutdoorTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TYPE, this.getName()));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_DIFFICULTY, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "6"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_IMPACT, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_OUTDOOR, "true"));
    }
    
    @Override
    public String getName() {
        return TaskType.Outdoor.name();
    }

}
