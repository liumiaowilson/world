package org.wilson.world.task;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public class LeisureTaskTemplateComponent extends AbstractTaskTemplateComponent {
    public LeisureTaskTemplateComponent() {
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_LEISURE);
        if(context != null) {
            this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
        }
        this.addTaskAttr(TaskAttr.create(TaskAttrDefManager.DEF_FUN, "50"));
        
        this.addTaskTemplateComponent(new NewFeatureTaskTemplateComponent());
        this.addTaskTemplateComponent(new BugFixTaskTemplateComponent());
        this.addTaskTemplateComponent(new RelationshipTaskTemplateComponent());
        this.addTaskTemplateComponent(new RoutineTaskTemplateComponent());
    }
    
    @Override
    public String getName() {
        return ContextManager.CONTEXT_LEISURE;
    }

}
