package org.wilson.world.task;

import java.util.List;

import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Task;

public class TaskIterator {
    private static TaskIterator instance;
    
    private boolean enabled;
    
    private int cursor;
    
    private TaskIterator() {
        
    }
    
    public static TaskIterator getInstance() {
        if(instance == null) {
            instance = new TaskIterator();
        }
        return instance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }
    
    public void setTaskId(int id) {
        List<Task> tasks = TaskManager.getInstance().getTasks();
        if(tasks.isEmpty()) {
            return;
        }
        
        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if(task.id == id) {
                this.setCursor(i);
                return;
            }
        }
    }
    
    public void reset() {
        this.setCursor(0);
    }

    public Task next() {
        List<Task> tasks = TaskManager.getInstance().getTasks();
        if(tasks.isEmpty()) {
            return null;
        }
        
        this.cursor += 1;
        if(this.cursor >= tasks.size()) {
            this.cursor = tasks.size() - 1;
        }
        
        return tasks.get(this.cursor);
    }
    
    public Task previous() {
        List<Task> tasks = TaskManager.getInstance().getTasks();
        if(tasks.isEmpty()) {
            return null;
        }
        
        this.cursor -= 1;
        if(this.cursor < 0) {
            this.cursor = 0;
        }
        
        return tasks.get(this.cursor);
    }
}
