package org.wilson.world.task;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.Task;

public interface TaskFollowerAction {
    public static final String EXTENSION_POINT = "task.follower";
    
    @Scriptable(name = EXTENSION_POINT, description = "Follow the task. Assign the value of this action to task follower.", params = { "task", "command" })
    public void interact(Task task, String command);
}
