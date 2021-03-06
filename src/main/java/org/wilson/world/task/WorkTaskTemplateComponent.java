package org.wilson.world.task;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public class WorkTaskTemplateComponent extends AbstractTaskTemplateComponent {
    public WorkTaskTemplateComponent() {
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_WORK);
        if(context != null) {
            this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
        }
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "80"));
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "80"));
        
        this.addTaskTemplateComponent(new NewFeatureTaskTemplateComponent());
        this.addTaskTemplateComponent(new BugFixTaskTemplateComponent());
        this.addTaskTemplateComponent(new RoutineTaskTemplateComponent());
        this.addTaskTemplateComponent(new ResearchTaskTemplateComponent());
    }
    
    @Override
    public String getName() {
        return ContextManager.CONTEXT_WORK;
    }

}
