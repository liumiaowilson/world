package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Task;
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
    }
    
    public Task getTask(int id) {
        Task task = this.dao.get(id);
        if(task != null) {
            return task;
        }
        else {
            return null;
        }
    }
    
    public List<Task> getTasks() {
        List<Task> result = new ArrayList<Task>();
        for(Task task : this.dao.getAll()) {
            result.add(task);
        }
        return result;
    }
    
    public void updateTask(Task task) {
        this.dao.update(task);
    }
    
    public void deleteTask(int id) {
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
