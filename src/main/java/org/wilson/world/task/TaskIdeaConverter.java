package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;

public class TaskIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Task";
    }

    @Override
    public Object convert(Idea idea) {
        Task task = new Task();
        task.name = idea.name;
        task.content = idea.content;
        long createdTime = System.currentTimeMillis();
        task.createdTime = createdTime;
        task.modifiedTime = createdTime;
        
        List<TaskAttr> attrs = new ArrayList<TaskAttr>();
        Map<String, String> defaultValues = TaskManager.getInstance().getTaskAttrDefaultValues();
        if(defaultValues != null) {
            for(Entry<String, String> entry : defaultValues.entrySet()) {
                TaskAttr attr = new TaskAttr();
                attr.name = entry.getKey();
                attr.value = entry.getValue();
                attrs.add(attr);
            }
        }
        task.attrs = attrs;
        
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
