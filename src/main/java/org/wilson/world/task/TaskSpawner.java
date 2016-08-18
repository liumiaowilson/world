package org.wilson.world.task;

import java.util.List;

import org.wilson.world.ext.Scriptable;
import org.wilson.world.model.Task;

public interface TaskSpawner {
    public static final String EXTENSION_POINT = "task.spawner";
    
    @Scriptable(name = EXTENSION_POINT, description = "Spawn new tasks. Assign the value of this action to task seed.", params = { "args" })
    public List<Task> spawnTasks(List<String> args);
}
