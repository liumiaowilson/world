package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.model.TaskFollower;
import org.wilson.world.task.DefaultTaskInteractor;
import org.wilson.world.task.DemoTaskInteractor;
import org.wilson.world.task.TaskFollowerAction;
import org.wilson.world.task.TaskInteractor;

public class TaskFollowerManager implements ItemTypeProvider, EventListener {
    private static final Logger logger = Logger.getLogger(TaskFollowerManager.class);
    
    public static final String NAME = "task_follower";
    
    private static TaskFollowerManager instance;
    
    private DAO<TaskFollower> dao = null;
    
    private Cache<String, TaskInteractor> cache = null;
    
    @SuppressWarnings("unchecked")
    private TaskFollowerManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskFollower.class);
        this.cache = new DefaultCache<String, TaskInteractor>("task_follower_manager_cache", false);
        ((CachedDAO<TaskFollower>)this.dao).getCache().addCacheListener(new CacheListener<TaskFollower>(){

            @Override
            public void cachePut(TaskFollower old, TaskFollower v) {
                if(old != null) {
                    TaskFollowerManager.this.cache.delete(old.symbol);
                }
                loadTaskFollower(v);
            }

            @Override
            public void cacheDeleted(TaskFollower v) {
                TaskFollowerManager.this.cache.delete(v.symbol);
            }

            @Override
            public void cacheLoaded(List<TaskFollower> all) {
                loadSystemTaskInteractors();
            }

            @Override
            public void cacheLoading(List<TaskFollower> old) {
                TaskFollowerManager.this.cache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        EventManager.getInstance().registerListener(EventType.FinishTask, this);
    }
    
    public static TaskFollowerManager getInstance() {
        if(instance == null) {
            instance = new TaskFollowerManager();
        }
        return instance;
    }
    
    private void loadSystemTaskInteractors() {
        this.loadSystemTaskInteractor(new DemoTaskInteractor());
    }
    
    private void loadSystemTaskInteractor(TaskInteractor interactor) {
        if(interactor == null) {
            return;
        }
        this.cache.put(interactor.getSymbol(), interactor);
    }
    
    @SuppressWarnings("rawtypes")
    private void loadTaskFollower(TaskFollower follower) {
        if(follower == null) {
            return;
        }
        String impl = follower.impl;
        if(StringUtils.isBlank(impl)) {
            return;
        }
        TaskFollowerAction action = null;
        try {
            Class clazz = Class.forName(impl);
            action = (TaskFollowerAction) clazz.newInstance();
            logger.info("Loaded task follower using class [" + impl + "]");
        }
        catch(Exception e) {
            action = (TaskFollowerAction) ExtManager.getInstance().wrapAction(impl, TaskFollowerAction.class);
            if(action != null) {
                logger.info("Loaded task follower using action [" + impl + "]");
            }
            else {
                logger.warn("Failed to load task follower using [" + impl + "]");
                return;
            }
        }
        
        DefaultTaskInteractor interactor = new DefaultTaskInteractor(follower, action);
        this.cache.put(interactor.getSymbol(), interactor);
    }
    
    public void createTaskFollower(TaskFollower follower) {
        this.dao.create(follower);
    }
    
    public TaskFollower getTaskFollower(int id) {
        TaskFollower follower = this.dao.get(id);
        if(follower != null) {
            return follower;
        }
        else {
            return null;
        }
    }
    
    public List<TaskFollower> getTaskFollowers() {
        List<TaskFollower> result = new ArrayList<TaskFollower>();
        for(TaskFollower follower : this.dao.getAll()) {
            result.add(follower);
        }
        return result;
    }
    
    public void updateTaskFollower(TaskFollower follower) {
        this.dao.update(follower);
    }
    
    public void deleteTaskFollower(int id) {
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
        return target instanceof TaskFollower;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskFollower follower = (TaskFollower)target;
        return String.valueOf(follower.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<TaskInteractor> getTaskInteractors() {
        return this.cache.getAll();
    }
    
    public TaskInteractor getTaskInteractor(String symbol) {
        if(StringUtils.isBlank(symbol)) {
            return null;
        }
        return this.cache.get(symbol);
    }
    
    public void interact(Task task) {
        if(task == null) {
            return;
        }
        
        TaskAttr attr = TaskManager.getInstance().getTaskAttr(task, TaskAttrDefManager.DEF_INTERACTOR);
        if(attr == null) {
            return;
        }
        String value = attr.value;
        if(StringUtils.isBlank(value)) {
            return;
        }
        
        int pos = value.indexOf(":");
        if(pos < 0) {
            return;
        }
        
        String symbol = value.substring(0, pos);
        String command = value.substring(pos + 1);
        TaskInteractor interactor = this.getTaskInteractor(symbol);
        if(interactor == null) {
            return;
        }
        
        interactor.interact(task, command);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        Task task = (Task) event.data.get("data");
        this.interact(task);
    }
}
