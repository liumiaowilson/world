package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
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
    
    private List<String> supported_types = new ArrayList<String>();
    
    private static TaskAttrDefManager instance;
    
    private DAO<TaskAttrDef> dao = null;
    
    @SuppressWarnings("unchecked")
    private TaskAttrDefManager() {
        this.initSupportedTypes();
        
        this.dao = DAOManager.getInstance().getCachedDAO(TaskAttrDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
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
    
    public static TaskAttrDefManager getInstance() {
        if(instance == null) {
            instance = new TaskAttrDefManager();
        }
        return instance;
    }
    
    public void createTaskAttrDef(TaskAttrDef def) {
        this.dao.create(def);
    }
    
    public TaskAttrDef getTaskAttrDef(int id) {
        TaskAttrDef def = this.dao.get(id);
        if(def != null) {
            return def;
        }
        else {
            return null;
        }
    }
    
    public List<TaskAttrDef> getTaskAttrDefs() {
        List<TaskAttrDef> result = new ArrayList<TaskAttrDef>();
        for(TaskAttrDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
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
