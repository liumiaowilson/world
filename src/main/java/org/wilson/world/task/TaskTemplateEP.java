package org.wilson.world.task;

import java.util.List;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.TaskAttr;

public interface TaskTemplateEP {
    public static final String EXTENSION_POINT = "task.template";
    
    @Scriptable(name = EXTENSION_POINT, description = "Create new task template. Assign the value of this action to config[task.templates].", params = {})
    public List<TaskAttr> getTemplateAttributes();
}
