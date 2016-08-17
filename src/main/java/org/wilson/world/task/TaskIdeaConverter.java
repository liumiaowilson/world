package org.wilson.world.task;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Task;

public class TaskIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Task";
    }

    @Override
    public Object convert(Idea idea) {
        Task task = Task.newTask(idea.name, idea.content);
        
        return task;
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToTask;
    }

    @Override
    public void save(Object converted) {
        TaskManager.getInstance().createTask((Task) converted);
    }

}
