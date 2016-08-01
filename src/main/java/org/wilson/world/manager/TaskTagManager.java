package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TaskTag;

public class TaskTagManager implements ItemTypeProvider {
    public static final String NAME = "task_tag";
    
    private static TaskTagManager instance;
    
    private DAO<TaskTag> dao = null;
    
    private Cache<Integer, TaskTag> cache = null;
    private Cache<String, Set<TaskTag>> map = null;
    
    @SuppressWarnings("unchecked")
    private TaskTagManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskTag.class);
        this.cache = new DefaultCache<Integer, TaskTag>("task_tag_manager_cache", false);
        this.map = new DefaultCache<String, Set<TaskTag>>("task_tag_manager_map", false);
        ((CachedDAO<TaskTag>)this.dao).getCache().addCacheListener(new CacheListener<TaskTag>(){

            @Override
            public void cachePut(TaskTag old, TaskTag v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                String [] tags = v.tags.split(",");
                for(String tag : tags) {
                    Set<TaskTag> tts = TaskTagManager.this.map.get(tag);
                    if(tts == null) {
                        tts = new HashSet<TaskTag>();
                        TaskTagManager.this.map.put(tag, tts);
                    }
                    tts.add(v);
                }
            }

            @Override
            public void cacheDeleted(TaskTag v) {
                TaskTagManager.this.cache.delete(v.taskId);
                
                String [] tags = v.tags.split(",");
                for(String tag : tags) {
                    Set<TaskTag> tts = TaskTagManager.this.map.get(tag);
                    if(tts != null) {
                        tts.remove(v);
                        if(tts.isEmpty()) {
                            TaskTagManager.this.map.delete(tag);
                        }
                    }
                }
            }

            @Override
            public void cacheLoaded(List<TaskTag> all) {
            }

            @Override
            public void cacheLoading(List<TaskTag> old) {
                TaskTagManager.this.cache.clear();
                TaskTagManager.this.map.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static TaskTagManager getInstance() {
        if(instance == null) {
            instance = new TaskTagManager();
        }
        return instance;
    }
    
    public void createTaskTag(TaskTag tag) {
        this.dao.create(tag);
    }
    
    public TaskTag getTaskTag(int id) {
        TaskTag tag = this.dao.get(id);
        if(tag != null) {
            return tag;
        }
        else {
            return null;
        }
    }
    
    public List<TaskTag> getTaskTags() {
        List<TaskTag> result = new ArrayList<TaskTag>();
        for(TaskTag tag : this.dao.getAll()) {
            result.add(tag);
        }
        return result;
    }
    
    public void updateTaskTag(TaskTag tag) {
        this.dao.update(tag);
    }
    
    public void deleteTaskTag(int id) {
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
        return target instanceof TaskTag;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskTag tag = (TaskTag)target;
        return String.valueOf(tag.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public TaskTag getTaskTagByTaskId(int taskId) {
        return this.cache.get(taskId);
    }
    
    public Set<TaskTag> getTaskTagsByTag(String tag) {
        if(StringUtils.isBlank(tag)) {
            return Collections.emptySet();
        }
        
        return this.map.get(tag);
    }
    
    public List<String> getTagNames() {
        return this.map.getKeys();
    }
}
