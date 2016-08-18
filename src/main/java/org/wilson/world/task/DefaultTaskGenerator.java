package org.wilson.world.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.manager.TaskSeedManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskSeed;

public class DefaultTaskGenerator implements TaskGenerator {
    private TaskSeed seed;
    private TaskSpawner spawner;
    private List<String> args = new ArrayList<String>();
    
    public DefaultTaskGenerator(TaskSeed seed, TaskSpawner spawner, List<String> args) {
        this.seed = seed;
        this.spawner = spawner;
        this.args = args;
    }
    
    @Override
    public String getName() {
        return this.seed.name;
    }

    @Override
    public boolean canStart(TimeZone tz, Date date) {
        return TaskSeedManager.getInstance().canStart(tz, date, this.seed.pattern);
    }

    @Override
    public List<Task> generateTasks() {
        return this.spawner.spawnTasks(args);
    }

}
