package org.wilson.world.task;

import org.wilson.world.model.Task;
import org.wilson.world.star.StarProvider;

public class TaskStarProvider implements StarProvider {

    @Override
    public boolean accept(Object target) {
        return target instanceof Task;
    }

    @Override
    public void star(Object target) {
        Task task = (Task) target;
        task.starred = true;
    }

    @Override
    public boolean isStarred(Object target) {
        Task task = (Task) target;
        return task.starred;
    }

    @Override
    public void unstar(Object target) {
        Task task = (Task) target;
        task.starred = false;
    }

    @Override
    public String getDisplayName(Object target) {
        Task task = (Task) target;
        return task.name;
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        Task t1 = (Task)o1;
        Task t2 = (Task)o2;
        return t1.id == t2.id;
    }

}
