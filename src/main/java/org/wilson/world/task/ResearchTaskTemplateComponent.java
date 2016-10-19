package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class ResearchTaskTemplateComponent extends AbstractTaskTemplateComponent {
    
    public ResearchTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TYPE, this.getName()));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_DIFFICULTY, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "4"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_IMPACT, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TOPIC, "NA"));
    }
    
    @Override
    public String getName() {
        return TaskType.Research.name();
    }

}
