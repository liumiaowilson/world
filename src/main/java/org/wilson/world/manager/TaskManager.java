package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.NumOfTasksMonitor;
import org.wilson.world.task.TaskStarProvider;

public class TaskManager implements ItemTypeProvider {
    public static final String NAME = "task";
    
    private static TaskManager instance;
    
    private DAO<Task> dao = null;
    
    @SuppressWarnings("unchecked")
    private TaskManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Task.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        StarManager.getInstance().registerStarProvider(new TaskStarProvider());
        
        int limit = ConfigManager.getInstance().getConfigAsInt("task.num.limit", 50);
        MonitorManager.getInstance().registerMonitorParticipant(new NumOfTasksMonitor(limit));
    }
    
    public static TaskManager getInstance() {
        if(instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
    
    public void createTask(Task task) {
        this.dao.create(task);
        
        for(TaskAttr attr : task.attrs) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().createTaskAttr(attr);
        }
    }
    
    public Task getTask(int id) {
        Task task = this.dao.get(id);
        if(task != null) {
            task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
            return task;
        }
        else {
            return null;
        }
    }
    
    public List<Task> getTasks() {
        List<Task> result = new ArrayList<Task>();
        for(Task task : this.dao.getAll()) {
            task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
            result.add(task);
        }
        return result;
    }
    
    private boolean hasTaskAttr(List<TaskAttr> attrs, TaskAttr attr) {
        for(TaskAttr p : attrs) {
            if(p.id == attr.id) {
                return true;
            }
        }
        return false;
    }
    
    public void updateTask(Task task) {
        this.dao.update(task);
        
        List<TaskAttr> oldAttrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
        List<TaskAttr> create = new ArrayList<TaskAttr>();
        List<TaskAttr> update = new ArrayList<TaskAttr>();
        List<TaskAttr> delete = new ArrayList<TaskAttr>();
        for(TaskAttr p : task.attrs) {
            if(p.id == 0) {
                create.add(p);
            }
            else if(hasTaskAttr(oldAttrs, p)) {
                update.add(p);
            }
            else {
                //will not get here
            }
        }
        for(TaskAttr p : oldAttrs) {
            if(!hasTaskAttr(task.attrs, p)) {
                delete.add(p);
            }
        }
        
        for(TaskAttr attr : create) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().createTaskAttr(attr);
        }
        
        for(TaskAttr attr : update) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().updateTaskAttr(attr);
        }
        
        for(TaskAttr attr : delete) {
            TaskAttrManager.getInstance().deleteTaskAttr(attr.id);
        }
    }
    
    public void deleteTask(int id) {
        Task oldTask = this.getTask(id);
        
        for(TaskAttr attr : oldTask.attrs) {
            TaskAttrManager.getInstance().deleteTaskAttr(attr.id);
        }
        
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Task;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Task task = (Task)target;
        return String.valueOf(task.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
