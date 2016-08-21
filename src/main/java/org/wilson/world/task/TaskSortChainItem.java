package org.wilson.world.task;

import org.wilson.world.model.Task;

public interface TaskSortChainItem {
    public TaskSortChainItem next();
    
    public SortResult sort(Task task1, Task task2);
}
