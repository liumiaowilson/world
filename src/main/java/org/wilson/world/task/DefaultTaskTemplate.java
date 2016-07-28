package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public abstract class DefaultTaskTemplate implements TaskTemplate {
    private List<TaskAttr> attrs = new ArrayList<TaskAttr>();
    
    public DefaultTaskTemplate() {
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "2"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "50"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "50"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_IMPACT, "50"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_FUN, "50"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_DIFFICULTY, "50"));
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_TOPIC, "None"));
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_LEISURE);
        if(context != null) {
            attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
        }
        attrs.add(TaskAttr.create(TaskAttrDefManager.DEF_ARTIFACT, "Unknown"));
    }
    
    protected TaskAttr getTaskAttr(List<TaskAttr> ret, String name) {
        for(TaskAttr attr : ret) {
            if(attr.name.equals(name)) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public List<TaskAttr> getTemplateAttributes() {
        return attrs;
    }

}
