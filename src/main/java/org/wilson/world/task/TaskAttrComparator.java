package org.wilson.world.task;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.TaskAttr;

public interface TaskAttrComparator {
    public static final String EXTENSION_POINT = "task.attr.sort";
    
    @Scriptable(name = EXTENSION_POINT, description = "Compare the task attributes. Assign this extension to the impl field of the task attr rule.", params = { "attr1", "attr2" })
    public int compare(TaskAttr attr1, TaskAttr attr2);
}
