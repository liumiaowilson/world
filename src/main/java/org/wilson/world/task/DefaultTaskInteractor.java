package org.wilson.world.task;

import org.wilson.world.model.Task;
import org.wilson.world.model.TaskFollower;

public class DefaultTaskInteractor implements TaskInteractor {
    private TaskFollower follower;
    private TaskFollowerAction action;
    
    public DefaultTaskInteractor(TaskFollower follower, TaskFollowerAction action) {
        this.follower = follower;
        this.action = action;
    }
    
    public int getID() {
        return this.follower.id;
    }
    
    public String getName() {
        return this.follower.name;
    }
    
    @Override
    public String getSymbol() {
        return this.follower.symbol;
    }

    @Override
    public void interact(Task task, String command) {
        this.action.interact(task, command);
    }

}
