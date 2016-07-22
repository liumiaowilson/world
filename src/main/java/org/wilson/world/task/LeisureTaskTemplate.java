package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public class LeisureTaskTemplate implements TaskTemplate {
    @Override
    public String getName() {
        return ContextManager.CONTEXT_LEISURE;
    }

    @Override
    public List<TaskAttr> getTemplateAttributes() {
        List<TaskAttr> ret = new ArrayList<TaskAttr>();
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "2"));
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "50"));
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "50"));
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_LEISURE);
        if(context != null) {
            ret.add(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
        }
        return ret;
    }

}
