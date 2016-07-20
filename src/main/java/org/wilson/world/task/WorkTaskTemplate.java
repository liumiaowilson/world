package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public class WorkTaskTemplate implements TaskTemplate {
    @Override
    public String getName() {
        return ContextManager.CONTEXT_WORK;
    }

    @Override
    public List<TaskAttr> getTemplateAttributes() {
        List<TaskAttr> ret = new ArrayList<TaskAttr>();
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_EFFORT, "3"));
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_PRIORITY, "60"));
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_URGENCY, "50"));
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_WORK);
        ret.add(TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id)));
        return ret;
    }

}