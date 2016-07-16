package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.TaskAttrDef;

public class TaskAttrDefManager implements ItemTypeProvider {
    public static final String NAME = "task_attr_def";
    
    public static final String TYPE_STRING = "String";
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_INTEGER = "Integer";
    public static final String TYPE_LONG = "Long";
    public static final String TYPE_DOUBLE = "Double";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_DATETIME = "DateTime";
    public static final String TYPE_TASK = "Task";
    
    public static final String DEF_BEFORE = "Before";
    public static final String DEF_AFTER = "After";
    
    public static final String DEF_PRIORITY = "Priority";
    public static final String DEF_URGENCY = "Urgency";
    public static final String DEF_LOCATION = "Location";
    public static final String DEF_CONTEXT = "Context";
    public static final String DEF_START_AT = "StartAt";
    public static final String DEF_DUE_AT = "DueAt";
    public static final String DEF_PARENT = "Parent";
    public static final String DEF_EFFORT = "Effort";
    
    private List<String> supported_types = new ArrayList<String>();
    
    private static TaskAttrDefManager instance;
    
    private DAO<TaskAttrDef> dao = null;
    private Cache<String, String> nameTypeCache = null;
    private Cache<Integer, TaskAttrDef> systemDefs = null;
    private int systemID = 1;
    
    @SuppressWarnings("unchecked")
    private TaskAttrDefManager() {
        this.systemDefs = new DefaultCache<Integer, TaskAttrDef>("task_attr_def_system_def", false);
        
        this.initSupportedTypes();
        this.loadSystemTaskAttrDef();
        
        this.dao = DAOManager.getInstance().getCachedDAO(TaskAttrDef.class);
        this.nameTypeCache = new DefaultCache<String, String>("task_attr_def_name_type_cache", false);
        this.reloadNameTypeCache();
        
        ((CachedDAO<TaskAttrDef>)this.dao).getCache().addCacheListener(new CacheListener<TaskAttrDef>(){
            @Override
            public void cachePut(TaskAttrDef v) {
                TaskAttrDefManager.this.nameTypeCache.put(v.name, v.type);
            }

            @Override
            public void cacheDeleted(TaskAttrDef v) {
                TaskAttrDefManager.this.nameTypeCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<TaskAttrDef> all) {
                for(TaskAttrDef def : all) {
                    TaskAttrDefManager.this.nameTypeCache.put(def.name, def.type);
                }
            }

            @Override
            public void cacheLoading(List<TaskAttrDef> old) {
                reloadNameTypeCache();
            }
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    private void reloadNameTypeCache() {
        this.nameTypeCache.clear();
        for(TaskAttrDef def : this.systemDefs.getAll()) {
            this.nameTypeCache.put(def.name, def.type);
        }
    }
    
    private void initSupportedTypes() {
        this.supported_types.add(TYPE_STRING);
        this.supported_types.add(TYPE_BOOLEAN);
        this.supported_types.add(TYPE_INTEGER);
        this.supported_types.add(TYPE_LONG);
        this.supported_types.add(TYPE_DOUBLE);
        this.supported_types.add(TYPE_DATE);
        this.supported_types.add(TYPE_DATETIME);
    }
    
    private void addSystemTaskAttrDef(TaskAttrDef def) {
        this.systemDefs.put(def.id, def);
    }
    
    private void loadSystemTaskAttrDef() {
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_BEFORE, TYPE_TASK, "This attribute modifies the task to go before another task.", true, true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_AFTER, TYPE_TASK, "This attribute modifies the task to go after another task.", true, true));
        
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_PRIORITY, TYPE_INTEGER, "This attribute modifies the priority of the task.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_URGENCY, TYPE_INTEGER, "This attribute modifies the urgency of the task.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_CONTEXT, TYPE_STRING, "This attribute modifies the context of the task.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_LOCATION, TYPE_STRING, "This attribute modifies the location of the task.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_START_AT, TYPE_DATETIME, "This attribute modifies the date time when the task starts.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_DUE_AT, TYPE_DATETIME, "This attribute modifies the date time when the task is due.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_PARENT, TYPE_TASK, "This attribute modifies the parent of the task.", true));
        addSystemTaskAttrDef(this.buildTaskAttrDef(DEF_EFFORT, TYPE_INTEGER, "This attribute modifies the effort in hours of the task.", true));
    }
    
    public TaskAttrDef buildTaskAttrDef(String name, String type, String description, boolean isSystem, boolean isPartialOrder) {
        TaskAttrDef def = new TaskAttrDef();
        def.name = name;
        def.type = type;
        def.description = description;
        def.isSystem = isSystem;
        def.isPartialOrder = isPartialOrder;
        if(def.isSystem) {
            def.id = -this.systemID++;
        }
        return def;
    }
    
    public TaskAttrDef buildTaskAttrDef(String name, String type, String description, boolean isSystem) {
        return this.buildTaskAttrDef(name, type, description, isSystem, false);
    }
    
    public TaskAttrDef buildTaskAttrDef(String name, String type, String description) {
        return this.buildTaskAttrDef(name, type, description, false);
    }
    
    public static TaskAttrDefManager getInstance() {
        if(instance == null) {
            instance = new TaskAttrDefManager();
        }
        return instance;
    }
    
    public void createTaskAttrDef(TaskAttrDef def) {
        if(this.nameTypeCache.get(def.name) != null) {
            throw new DataException("Task attr def with name [" + def.name + "] already exists!");
        }
        
        this.dao.create(def);
    }
    
    public TaskAttrDef getTaskAttrDef(int id) {
        TaskAttrDef def = this.systemDefs.get(id);
        if(def != null) {
            return def;
        }
        else {
            def = this.dao.get(id);
            if(def != null) {
                return def;
            }
            else {
                return null;
            }
        }
    }
    
    public List<TaskAttrDef> getTaskAttrDefs() {
        List<TaskAttrDef> result = new ArrayList<TaskAttrDef>();
        for(TaskAttrDef def : this.systemDefs.getAll()) {
            result.add(def);
        }
        for(TaskAttrDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public List<String> getTaskAttrNames() {
        return this.nameTypeCache.getKeys();
    }
    
    public String getTaskAttrType(String name) {
        return this.nameTypeCache.get(name);
    }
    
    public void updateTaskAttrDef(TaskAttrDef def) {
        this.dao.update(def);
    }
    
    public void deleteTaskAttrDef(int id) {
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
        return target instanceof TaskAttrDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskAttrDef def = (TaskAttrDef)target;
        return String.valueOf(def.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<String> getSupportedTypes() {
        return this.supported_types;
    }
}
