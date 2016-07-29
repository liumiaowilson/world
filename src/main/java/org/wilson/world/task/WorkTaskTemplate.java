package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.ContextManager;
import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.Context;
import org.wilson.world.model.TaskAttr;

public class WorkTaskTemplate extends DefaultTaskTemplate {
    @Override
    public String getName() {
        return ContextManager.CONTEXT_WORK;
    }

    @Override
    public List<TaskAttr> getTemplateAttributes() {
        List<TaskAttr> ret = super.getTemplateAttributes();
        Context context = ContextManager.getInstance().getContext(ContextManager.CONTEXT_WORK);
        if(context != null) {
            TaskAttr attr = TaskAttr.getTaskAttr(ret, TaskAttrDefManager.DEF_CONTEXT);
            if(attr != null) {
                attr.value = String.valueOf(context.id);
            }
            else {
                attr = TaskAttr.create(TaskAttrDefManager.DEF_CONTEXT, String.valueOf(context.id));
                ret.add(attr);
            }
        }
        return ret;
    }

}
