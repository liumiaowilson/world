package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class RoutineTaskTemplateComponent extends AbstractTaskTemplateComponent {
    public static final String NAME = "Routine";
    
    public RoutineTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TYPE, NAME));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "2"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TOPIC, "NA"));
    }
    
    @Override
    public String getName() {
        return NAME;
    }

}
