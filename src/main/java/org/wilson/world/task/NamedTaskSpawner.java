package org.wilson.world.task;

import java.util.Arrays;
import java.util.List;

import org.wilson.world.model.Task;

public class NamedTaskSpawner implements TaskSpawner {

    @Override
    public List<Task> spawnTasks(List<String> args) {
        String name = "Unknown";
        if(!args.isEmpty()) {
            name = args.get(0);
        }
        
        Task task = new Task();
        task.name = name;
        task.content = name;
        task.createdTime = System.currentTimeMillis();
        task.modifiedTime = task.createdTime;
        return Arrays.asList(task);
    }

}
