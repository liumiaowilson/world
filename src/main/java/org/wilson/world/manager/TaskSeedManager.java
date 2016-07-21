package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TaskSeed;

public class TaskSeedManager implements ItemTypeProvider {
    public static final String NAME = "task_seed";
    
    private static TaskSeedManager instance;
    
    private DAO<TaskSeed> dao = null;
    
    @SuppressWarnings("unchecked")
    private TaskSeedManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskSeed.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static TaskSeedManager getInstance() {
        if(instance == null) {
            instance = new TaskSeedManager();
        }
        return instance;
    }
    
    public void createTaskSeed(TaskSeed seed) {
        this.dao.create(seed);
    }
    
    public TaskSeed getTaskSeed(int id) {
        TaskSeed seed = this.dao.get(id);
        if(seed != null) {
            return seed;
        }
        else {
            return null;
        }
    }
    
    public List<TaskSeed> getTaskSeeds() {
        List<TaskSeed> result = new ArrayList<TaskSeed>();
        for(TaskSeed seed : this.dao.getAll()) {
            result.add(seed);
        }
        return result;
    }
    
    public void updateTaskSeed(TaskSeed seed) {
        this.dao.update(seed);
    }
    
    public void deleteTaskSeed(int id) {
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
        return target instanceof TaskSeed;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskSeed seed = (TaskSeed)target;
        return String.valueOf(seed.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
