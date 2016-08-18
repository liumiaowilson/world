package org.wilson.world.task;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.model.Task;

public interface TaskGenerator {
    public String getName();
    
    public boolean canStart(TimeZone tz, Date date);
    
    public List<Task> generateTasks();
}
