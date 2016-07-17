package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.task.NumOfTasksMonitor;
import org.wilson.world.task.TaskDefaultValueProvider;
import org.wilson.world.task.TaskSortChainItem;
import org.wilson.world.task.TaskStarProvider;

public class TaskManager implements ItemTypeProvider {
    public static final String NAME = "task";
    
    private static TaskManager instance;
    
    private DAO<Task> dao = null;
    
    private Map<Integer, Set<Integer>> dep = null;
    
    private Map<String, String> taskAttrDefaultValues = null;
    
    @SuppressWarnings("unchecked")
    private TaskManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Task.class);
        this.dep = new HashMap<Integer, Set<Integer>>();
        ((CachedDAO<Task>)this.dao).getCache().addCacheListener(new CacheListener<Task>(){
            @Override
            public void cachePut(Task v) {
                //attributes are not persisted when created by now
            }

            @Override
            public void cacheDeleted(Task v) {
                Map<Integer, Integer> d = TaskManager.this.getDependency(v);
                for(Entry<Integer, Integer> entry : d.entrySet()) {
                    int id1 = entry.getKey();
                    int id2 = entry.getValue();
                    Set<Integer> ids = TaskManager.this.dep.get(id1);
                    if(ids != null) {
                        ids.remove(id2);
                    }
                }
            }

            @Override
            public void cacheLoaded(List<Task> all) {
                for(Task task : all) {
                    task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
                    addTaskToDep(task);
                }
            }

            @Override
            public void cacheLoading(List<Task> old) {
                TaskManager.this.dep.clear();
            }
        });
        
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
        for(TaskAttr attr : task.attrs) {
            TaskAttrManager.getInstance().processTaskAttr(attr);
        }
        
        if(!this.isValidDep(task)) {
            throw new DataException("Invalid dependency detected for task [" + task.name + "].");
        }
        
        this.dao.create(task);
        
        for(TaskAttr attr : task.attrs) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().createTaskAttr(attr);
        }
        
        this.addTaskToDep(task);
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
    
    public Task getTask(String name) {
        for(Task task : this.dao.getAll()) {
            if(task.name.equals(name)) {
                task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
                return task;
            }
        }
        return null;
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
        for(TaskAttr attr : task.attrs) {
            TaskAttrManager.getInstance().processTaskAttr(attr);
        }
        
        if(!this.isValidDep(task)) {
            throw new DataException("Invalid dependency detected for task [" + task.name + "].");
        }
        
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
        
        this.addTaskToDep(task);
    }
    
    private void addTaskToDep(Task task) {
        Map<Integer, Integer> d = TaskManager.this.getDependency(task);
        for(Entry<Integer, Integer> entry : d.entrySet()) {
            int id1 = entry.getKey();
            int id2 = entry.getValue();
            Set<Integer> ids = TaskManager.this.dep.get(id1);
            if(ids == null) {
                ids = new HashSet<Integer>();
                TaskManager.this.dep.put(id1, ids);
            }
            ids.add(id2);
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
    
    public TaskAttr getTaskAttr(Task task, String attrName) {
        if(task == null || StringUtils.isBlank(attrName)) {
            return null;
        }
        if(!TaskAttrDefManager.getInstance().isValidTaskAttrName(attrName)) {
            return null;
        }
        for(TaskAttr attr : task.attrs) {
            if(attr.name.equals(attrName)) {
                return attr;
            }
        }
        return null;
    }
    
    public List<Task> getSortedTasks() {
        List<Task> ret = new ArrayList<Task>();
        for(Task task : this.getTasks()) {
            ret.add(task);
        }
        
        final TaskSortChainItem chain = TaskAttrRuleManager.getInstance().getTaskSortChainItem();
        if(chain != null) {
            Collections.sort(ret, new Comparator<Task>(){
                @Override
                public int compare(Task o1, Task o2) {
                    if(isBefore(o1, o2)) {
                        return -1;
                    }
                    else if(isAfter(o1, o2)) {
                        return 1;
                    }
                    else {
                        return chain.sort(o1, o2);
                    }
                }
            });
        }
        
        return ret;
    }
    
    public List<Task> getTodos() {
        List<Task> all = this.getSortedTasks();
        int limit = ConfigManager.getInstance().getConfigAsInt("todo.view.limit", 10);
        return all.subList(0, limit);
    }
    
    public Map<Integer, Integer> getDependency(Task task) {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        if(task == null) {
            return ret;
        }
        
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_BEFORE);
        if(attr != null) {
            try {
                int id = Integer.parseInt(attr.value);
                ret.put(id, task.id);
            }
            catch(Exception e) {
            }
        }
        
        attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_AFTER);
        if(attr != null) {
            try {
                int id = Integer.parseInt(attr.value);
                ret.put(task.id, id);
            }
            catch(Exception e) {
            }
        }
        
        return ret;
    }
    
    public boolean isValidDep(Task task) {
        Map<Integer, Integer> d = this.getDependency(task);
        
        for(Entry<Integer, Integer> entry : d.entrySet()) {
            int id1 = entry.getKey();
            int id2 = entry.getValue();
            Set<Integer> ids = this.getDependentTaskIds(id2);
            if(ids != null) {
                if(ids.contains(id1)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public Set<Integer> getDependentTaskIds(int taskId) {
        Set<Integer> ret = new HashSet<Integer>();
        
        this.getDependentTaskIds(taskId, ret);
        
        return ret;
    }
    
    private void getDependentTaskIds(int taskId, Set<Integer> ids) {
        Set<Integer> d = this.dep.get(taskId);
        if(d != null) {
            ids.addAll(d);
            for(int did : d) {
                getDependentTaskIds(did, ids);
            }
        }
    }
    
    public boolean isBefore(Task task1, Task task2) {
        if(task1 == null || task2 == null) {
            return false;
        }
        
        Set<Integer> ids = this.getDependentTaskIds(task2.id);
        return ids.contains(task1.id);
    }
    
    public boolean isAfter(Task task1, Task task2) {
        if(task1 == null || task2 == null) {
            return false;
        }
        
        Set<Integer> ids = this.getDependentTaskIds(task1.id);
        return ids.contains(task2.id);
    }
    
    public Map<String, String> getTaskAttrDefaultValues() {
        if(this.taskAttrDefaultValues == null) {
            this.taskAttrDefaultValues = new HashMap<String, String>();
            TaskDefaultValueProvider provider = ExtManager.getInstance().getExtension(TaskDefaultValueProvider.class);
            if(provider != null) {
                for(String name : TaskAttrDefManager.getInstance().getTaskAttrNames()) {
                    String value = provider.getDefaultValue(name);
                    if(value != null) {
                        this.taskAttrDefaultValues.put(name, value);
                    }
                }
            }
        }
        return this.taskAttrDefaultValues;
    }
}
