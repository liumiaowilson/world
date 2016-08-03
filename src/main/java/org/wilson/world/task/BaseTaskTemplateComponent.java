package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class BaseTaskTemplateComponent extends AbstractTaskTemplateComponent {
    public BaseTaskTemplateComponent() {
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "50"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_ARTIFACT, "NA"));
        
        this.addTaskTemplateComponent(new LeisureTaskTemplateComponent());
        this.addTaskTemplateComponent(new WorkTaskTemplateComponent());
    }
    
    @Override
    public String getName() {
        return "";
    }

}
