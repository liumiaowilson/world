package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class OutdoorTaskTemplateComponent extends AbstractTaskTemplateComponent {
    public static final String NAME = "Outdoor";
    
    public OutdoorTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_TYPE, NAME));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_DIFFICULTY, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "6"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_IMPACT, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_OUTDOOR, "true"));
    }
    
    @Override
    public String getName() {
        return NAME;
    }

}
