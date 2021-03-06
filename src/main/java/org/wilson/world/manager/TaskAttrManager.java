package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.context.TaskAttrContextDBCleaner;
import org.wilson.world.dao.DAO;
import org.wilson.world.document.TaskAttrDocumentDBCleaner;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Context;
import org.wilson.world.model.Document;
import org.wilson.world.model.Plan;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.model.TaskAttrDef;
import org.wilson.world.task.TaskAttrDBCleaner;

public class TaskAttrManager implements ItemTypeProvider {
    public static final String NAME = "task_attr";
    
    private static TaskAttrManager instance;
    
    private DAO<TaskAttr> dao = null;
    
    @SuppressWarnings("unchecked")
    private TaskAttrManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(TaskAttr.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new TaskAttrContextDBCleaner());
        ItemManager.getInstance().addDBCleaner(new TaskAttrDocumentDBCleaner());
        ItemManager.getInstance().addDBCleaner(new TaskAttrDBCleaner());
    }
    
    public static TaskAttrManager getInstance() {
        if(instance == null) {
            instance = new TaskAttrManager();
        }
        return instance;
    }
    
    public void createTaskAttr(TaskAttr attr) {
        ItemManager.getInstance().checkDuplicate(attr);
        
        this.dao.create(attr);
    }
    
    public void processTaskAttr(TaskAttr attr) {
        if(attr == null) {
            return;
        }
        TaskAttrDef def = TaskAttrDefManager.getInstance().getTaskAttrDef(attr.name);
        if(def != null) {
            if(TaskAttrDefManager.TYPE_TASK.equals(def.type)) {
                try {
                    Integer.parseInt(attr.value);
                }
                catch(Exception e) {
                    Task task = TaskManager.getInstance().getTask(attr.value);
                    attr.value = String.valueOf(task.id);
                }
            }
            else if(TaskAttrDefManager.TYPE_CONTEXT.equals(def.type)) {
                try {
                    Integer.parseInt(attr.value);
                }
                catch(Exception e) {
                    Context context = ContextManager.getInstance().getContext(attr.value);
                    attr.value = String.valueOf(context.id);
                }
            }
            else if(TaskAttrDefManager.TYPE_DOCUMENT.equals(def.type)) {
                try {
                    Integer.parseInt(attr.value);
                }
                catch(Exception e) {
                    Document document = DocumentManager.getInstance().getDocument(attr.value);
                    attr.value = String.valueOf(document.id);
                }
            }
            else if(TaskAttrDefManager.TYPE_PLAN.equals(def.type)) {
                try {
                    Integer.parseInt(attr.value);
                }
                catch(Exception e) {
                    Plan plan = PlanManager.getInstance().getPlan(attr.value);
                    attr.value = String.valueOf(plan.id);
                }
            }
        }
    }
    
    public TaskAttr getTaskAttrFromDB(int id) {
        return this.dao.get(id);
    }
    
    public TaskAttr getTaskAttr(int id) {
        TaskAttr attr = this.dao.get(id);
        if(attr != null) {
            return attr;
        }
        else {
            return null;
        }
    }
    
    public List<TaskAttr> getTaskAttrs() {
        return this.dao.getAll();
    }
    
    public void updateTaskAttr(TaskAttr attr) {
        this.dao.update(attr);
    }
    
    public void deleteTaskAttr(int id) {
        this.dao.delete(id);
    }
    
    public List<TaskAttr> getTaskAttrsByTaskId(int taskId) {
        List<TaskAttr> ret = new ArrayList<TaskAttr>();
        for(TaskAttr attr : this.dao.getAll()) {
            if(attr.taskId == taskId) {
                ret.add(attr);
            }
        }
        Collections.sort(ret, new Comparator<TaskAttr>(){
            @Override
            public int compare(TaskAttr o1, TaskAttr o2) {
                return o1.id - o2.id;
            }
        });
        return ret;
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
        return target instanceof TaskAttr;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskAttr attr = (TaskAttr)target;
        return String.valueOf(attr.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public TaskAttr copyTaskAttr(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        TaskAttr copy = new TaskAttr();
        copy.name = attr.name;
        copy.value = attr.value;
        return copy;
    }
    
    public Object getRealValue(String attrName, String attrValue) {
        TaskAttr attr = new TaskAttr();
        attr.name = attrName;
        attr.value = attrValue;
        return this.getRealValue(attr);
    }
    
    public Object getRealValue(TaskAttr attr) {
        TaskAttrDef def = TaskAttrDefManager.getInstance().getTaskAttrDef(attr.name);
        if(def != null) {
            if(TaskAttrDefManager.TYPE_TASK.equals(def.type)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Task task = TaskManager.getInstance().getTask(id);
                    if(task != null) {
                        return task.name;
                    }
                }
                catch(Exception e) {
                    return attr.value;
                }
            }
            else if(TaskAttrDefManager.TYPE_CONTEXT.equals(def.type)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Context context = ContextManager.getInstance().getContext(id);
                    if(context != null) {
                        return context.name;
                    }
                }
                catch(Exception e) {
                    return attr.value;
                }
            }
            else if(TaskAttrDefManager.TYPE_DOCUMENT.equals(def.type)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Document doc = DocumentManager.getInstance().getDocument(id);
                    if(doc != null) {
                        return doc.name;
                    }
                }
                catch(Exception e) {
                    return attr.value;
                }
            }
            else if(TaskAttrDefManager.TYPE_PLAN.equals(def.type)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Plan plan = PlanManager.getInstance().getPlan(id);
                    if(plan != null) {
                        return plan.name;
                    }
                }
                catch(Exception e) {
                    return attr.value;
                }
            }
        }
        
        return attr.value;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        TaskAttr attr = (TaskAttr)target;
        return attr.taskId + "_" + attr.name;
    }
}
