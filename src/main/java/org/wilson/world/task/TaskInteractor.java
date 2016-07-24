package org.wilson.world.task;

import org.wilson.world.model.Task;

public interface TaskInteractor {
    public String getSymbol();
    
    public void interact(Task task, String command);
}
