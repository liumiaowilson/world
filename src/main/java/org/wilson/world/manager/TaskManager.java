package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.model.Context;
import org.wilson.world.model.Document;
import org.wilson.world.model.Plan;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;
import org.wilson.world.model.TaskDepEdge;
import org.wilson.world.model.TaskDepNode;
import org.wilson.world.model.TaskInfo;
import org.wilson.world.model.TaskProjectEdge;
import org.wilson.world.model.TaskProjectNode;
import org.wilson.world.model.TaskTag;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.task.IncompleteTaskMonitor;
import org.wilson.world.task.NumOfTasksMonitor;
import org.wilson.world.task.ReviewTaskMonitor;
import org.wilson.world.task.SortResult;
import org.wilson.world.task.TaskDefaultValueProvider;
import org.wilson.world.task.TaskIdeaConverter;
import org.wilson.world.task.TaskSortChainItem;
import org.wilson.world.task.TaskStarProvider;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;

public class TaskManager implements ItemTypeProvider {
    public static final String NAME = "task";
    
    private static TaskManager instance;
    
    private DAO<Task> dao = null;
    
    private Map<Integer, Set<Integer>> dep = null;
    
    /* For simplicity, only one level project is allowed */
    private Map<Integer, Set<Integer>> projects = null;
    
    private Map<String, String> taskAttrDefaultValues = null;
    
    private Cache<String, Set<Task>> tagCache = null;
    
    private IncompleteTaskMonitor incompleteTaskMonitor = null;
    private ReviewTaskMonitor reviewTaskMonitor = null;
    
    @SuppressWarnings("unchecked")
    private TaskManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Task.class);
        this.tagCache = new DefaultCache<String, Set<Task>>("task_manager_tag_cache", false);
        this.dep = new HashMap<Integer, Set<Integer>>();
        this.projects = new HashMap<Integer, Set<Integer>>();
        ((CachedDAO<Task>)this.dao).getCache().addCacheListener(new CacheListener<Task>(){
            @Override
            public void cachePut(Task old, Task v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                addToTagCache(v);
                
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
                
                TaskAttr attr = TaskAttr.getTaskAttr(v.attrs, TaskAttrDefManager.DEF_PARENT);
                if(attr != null) {
                    try {
                        int p_id = Integer.parseInt(attr.value);
                        Set<Integer> children = TaskManager.this.projects.get(p_id);
                        if(children != null) {
                            children.remove(v.id);
                        }
                    }
                    catch(Exception e) {
                    }
                }
                
                removeFromTagCache(v);
            }

            @Override
            public void cacheLoaded(List<Task> all) {
                for(Task task : all) {
                    task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
                    addTaskToDep(task);
                    addTaskToProjects(task);
                }
            }

            @Override
            public void cacheLoading(List<Task> old) {
                TaskManager.this.dep.clear();
                TaskManager.this.projects.clear();
                TaskManager.this.tagCache.clear();
            }
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        StarManager.getInstance().registerStarProvider(new TaskStarProvider());
        
        int limit = ConfigManager.getInstance().getConfigAsInt("task.num.limit", 50);
        MonitorManager.getInstance().registerMonitorParticipant(new NumOfTasksMonitor(limit));
        
        this.incompleteTaskMonitor = new IncompleteTaskMonitor();
        MonitorManager.getInstance().registerMonitorParticipant(this.incompleteTaskMonitor);
        this.reviewTaskMonitor = new ReviewTaskMonitor();
        MonitorManager.getInstance().registerMonitorParticipant(this.reviewTaskMonitor);;
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new TaskIdeaConverter());
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Task task : getTasks()) {
                    boolean found = task.name.contains(text) || task.content.contains(text);
                    if(!found) {
                        for(TaskAttr attr : task.attrs) {
                            if(!StringUtils.isBlank(attr.value)) {
                                if(attr.value.contains(text)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if(!found) {
                        TaskTag tag = task.tag;
                        if(tag != null) {
                            if(!StringUtils.isBlank(tag.tags)) {
                                found = tag.tags.contains(text);
                            }
                        }
                    }
                    if(found) {
                        Content content = new Content();
                        content.id = task.id;
                        content.name = task.name;
                        content.description = task.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static TaskManager getInstance() {
        if(instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
    
    public MonitorParticipant getIncompleteTaskMonitor() {
        return this.incompleteTaskMonitor;
    }
    
    public MonitorParticipant getReviewTaskMonitor() {
        return this.reviewTaskMonitor;
    }
    
    private void addToTagCache(Task task) {
        if(task == null) {
            return;
        }
        String name = task.name;
        String [] items = name.split(" ");
        for(String item : items) {
            item = item.trim().toLowerCase();
            if(StringUtils.isBlank(item)) {
                continue;
            }
            Set<Task> tasks = this.tagCache.get(item);
            if(tasks == null) {
                tasks = new HashSet<Task>();
                this.tagCache.put(item, tasks);
            }
            tasks.add(task);
        }
    }
    
    private void removeFromTagCache(Task task) {
        if(task == null) {
            return;
        }
        String name = task.name;
        String [] items = name.split(" ");
        for(String item : items) {
            item = item.trim().toLowerCase();
            if(StringUtils.isBlank(item)) {
                continue;
            }
            Set<Task> tasks = this.tagCache.get(item);
            if(tasks != null) {
                tasks.remove(tasks);
            }
        }
    }
    
    public void createTask(Task task) {
        if(this.hasTask(task)) {
            throw new DataException("Task with same name already exists.");
        }
        
        for(TaskAttr attr : task.attrs) {
            TaskAttrManager.getInstance().processTaskAttr(attr);
        }
        
        if(!this.isValidDep(task)) {
            throw new DataException("Invalid dependency detected for task [" + task.name + "].");
        }
        
        if(!this.isValidProject(task)) {
            throw new DataException("Invalid nested projects detected for task [" + task.name + "].");
        }
        
        ItemManager.getInstance().checkDuplicate(task);
        
        this.dao.create(task);
        
        for(TaskAttr attr : task.attrs) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().createTaskAttr(attr);
        }

        this.addTaskToDep(task);
        
        this.addTaskToProjects(task);
        
        TaskTag tag = task.tag;
        if(tag != null) {
            tag.taskId = task.id;
            TaskTagManager.getInstance().createTaskTag(tag);
        }
    }
    
    public Task getTask(int id) {
        Task task = this.dao.get(id);
        if(task != null) {
            this.loadTask(task);
            return task;
        }
        else {
            return null;
        }
    }
    
    public boolean hasTask(Task task) {
        if(task == null) {
            return false;
        }
        for(Task t : this.dao.getAll()) {
            if(t.name.equals(task.name) && t.id != task.id) {
                return true;
            }
        }
        return false;
    }
    
    public Task getTask(String name) {
        for(Task task : this.dao.getAll()) {
            if(task.name.equals(name)) {
                this.loadTask(task);
                return task;
            }
        }
        return null;
    }
    
    public List<Task> getTasks() {
        List<Task> result = new ArrayList<Task>();
        for(Task task : this.dao.getAll()) {
            this.loadTask(task);
            result.add(task);
        }
        return result;
    }
    
    public void loadTask(Task task) {
        task.attrs = TaskAttrManager.getInstance().getTaskAttrsByTaskId(task.id);
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_SEED);
        if(attr != null) {
            task.seed = this.getSeedHint(attr);
        }
        else {
            task.seed = null;
        }
        attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_INTERACTOR);
        if(attr != null) {
            task.follower = this.getFollowerHint(attr);
        }
        else {
            task.follower = null;
        }
        attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_CONTEXT);
        if(attr != null) {
            task.context = this.getContextHint(attr);
        }
        else {
            task.context = null;
        }
        attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_TYPE);
        if(attr != null) {
            task.type = this.getTypeHint(attr);
        }
        else {
            task.type = null;
        }
        
        TaskTag tag = TaskTagManager.getInstance().getTaskTagByTaskId(task.id);
        if(tag != null) {
            task.tag = tag;
        }
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
        if(this.hasTask(task)) {
            throw new DataException("Task with same name already exists.");
        }
        
        for(TaskAttr attr : task.attrs) {
            TaskAttrManager.getInstance().processTaskAttr(attr);
        }
        
        if(!this.isValidDep(task)) {
            throw new DataException("Invalid dependency detected for task [" + task.name + "].");
        }
        
        if(!this.isValidProject(task)) {
            throw new DataException("Invalid nested projects detected for task [" + task.name + "].");
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

        for(TaskAttr attr : delete) {
            TaskAttrManager.getInstance().deleteTaskAttr(attr.id);
            this.removeTaskDep(task, attr);
            this.removeTaskProject(task, attr);
        }
        
        for(TaskAttr attr : create) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().createTaskAttr(attr);
        }
        
        for(TaskAttr attr : update) {
            attr.taskId = task.id;
            TaskAttrManager.getInstance().updateTaskAttr(attr);
        }
        
        this.addTaskToDep(task);
        
        this.addTaskToProjects(task);
        
        TaskTag tag = task.tag;
        if(tag != null) {
            tag.taskId = task.id;
            TaskTag oldTag = TaskTagManager.getInstance().getTaskTagByTaskId(task.id);
            if(oldTag == null) {
                TaskTagManager.getInstance().createTaskTag(tag);
            }
            else {
                if(!oldTag.tags.equals(tag.tags)) {
                    oldTag.tags = tag.tags;
                    TaskTagManager.getInstance().updateTaskTag(oldTag);
                }
            }
        }
        else {
            TaskTag oldTag = TaskTagManager.getInstance().getTaskTagByTaskId(task.id);
            if(oldTag != null) {
                TaskTagManager.getInstance().deleteTaskTag(oldTag.id);
            }
        }
    }
    
    private void removeTaskProject(Task task, TaskAttr attr) {
        if(TaskAttrDefManager.DEF_PARENT.equals(attr.name)) {
            try{
                int p_id = Integer.parseInt(attr.value);
                Set<Integer> children = this.projects.get(p_id);
                if(children != null) {
                    children.remove(task.id);
                }
            }
            catch(Exception e) {
            }
        }
    }
    
    private void removeTaskDep(Task task, TaskAttr attr) {
        if(TaskAttrDefManager.DEF_BEFORE.equals(attr.name)) {
            try{
                int id = Integer.parseInt(attr.value);
                Set<Integer> deps = this.dep.get(id);
                if(deps != null) {
                    deps.remove(task.id);
                }
            }
            catch(Exception e) {
            }
        }
        else if(TaskAttrDefManager.DEF_AFTER.equals(attr.name)) {
            try{
                int id = Integer.parseInt(attr.value);
                Set<Integer> deps = this.dep.get(task.id);
                if(deps != null) {
                    deps.remove(id);
                }
            }
            catch(Exception e) {
            }
        }
    }
    
    private void addTaskToProjects(Task task) {
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_PARENT);
        if(attr != null) {
            try {
                int p_id = Integer.parseInt(attr.value);
                Set<Integer> children = this.projects.get(p_id);
                if(children == null) {
                    children = new HashSet<Integer>();
                    this.projects.put(p_id, children);
                }
                children.add(task.id);
            }
            catch(Exception e){
            }
        }
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
        if(oldTask == null) {
            return;
        }
        
        TaskTag tag = TaskTagManager.getInstance().getTaskTagByTaskId(id);
        if(tag != null) {
            TaskTagManager.getInstance().deleteTaskTag(tag.id);
        }
        
        List<Task> impacts = new ArrayList<Task>();
        if(this.dep.containsKey(id)) {
            for(int sid : this.dep.get(id)) {
                impacts.add(this.getTask(sid));
            }
            this.dep.remove(id);
        }
        for(Entry<Integer, Set<Integer>> entry : this.dep.entrySet()) {
            int eid = entry.getKey();
            Set<Integer> set = entry.getValue();
            if(set.contains(id)) {
                impacts.add(this.getTask(eid));
                set.remove(id);
            }
        }
        
        for(Task t : impacts) {
            if(t.id != oldTask.id) {
                boolean updated = false;
                TaskAttr attr = this.getTaskAttr(t, TaskAttrDefManager.DEF_BEFORE);
                if(attr != null) {
                    if(String.valueOf(oldTask.id).equals(attr.value)) {
                        t.attrs.remove(attr);
                        updated = true;
                    }
                }
                attr = this.getTaskAttr(t, TaskAttrDefManager.DEF_AFTER);
                if(attr != null) {
                    if(String.valueOf(oldTask.id).equals(attr.value)) {
                        t.attrs.remove(attr);
                        updated = true;
                    }
                }
                if(updated) {
                    this.updateTask(t);
                }
            }
        }
        
        Set<Integer> children = this.projects.get(id);
        if(children != null) {
            for(Integer child_id : children) {
                Task child = this.getTask(child_id);
                if(child != null) {
                    TaskAttr attr = TaskAttr.getTaskAttr(child.attrs, TaskAttrDefManager.DEF_PARENT);
                    if(attr != null && attr.value != null && attr.value.equals(String.valueOf(id))) {
                        child.attrs.remove(attr);
                        this.updateTask(child);
                    }
                }
            }
        }
        
        for(TaskAttr attr : oldTask.attrs) {
            TaskAttrManager.getInstance().deleteTaskAttr(attr.id);
        }
        
        this.dao.delete(id);
    }
    
    public Set<Task> getReferencedTasks(Task task) {
        Set<Task> ret = new HashSet<Task>();
        if(task != null) {
            for(Task t : this.getTasks()) {
                if(t.id != task.id) {
                    TaskAttr attr = this.getTaskAttr(t, TaskAttrDefManager.DEF_BEFORE);
                    if(attr != null) {
                        if(String.valueOf(task.id).equals(attr.value)) {
                            ret.add(t);
                        }
                    }
                    attr = this.getTaskAttr(t, TaskAttrDefManager.DEF_AFTER);
                    if(attr != null) {
                        if(String.valueOf(task.id).equals(attr.value)) {
                            ret.add(t);
                        }
                    }
                }
            }
        }
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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
    
    public Task findTask(String attrName, String attrValue) {
        for(Task task : this.getTasks()) {
            TaskAttr attr = this.getTaskAttr(task, attrName);
            if(attr != null && attr.value != null && attr.value.equals(attrValue)) {
                return task;
            }
        }
        return null;
    }
    
    public List<Task> getIndividualTasks() {
        List<Task> ret = new ArrayList<Task>();
        for(Task task : this.getTasks()) {
            Set<Integer> ids = this.getDependentTaskIds(task.id);
            if(ids != null && !ids.isEmpty()) {
                continue;
            }
            
            Set<Integer> children = this.projects.get(task.id);
            if(children != null && !children.isEmpty()) {
                continue;
            }
            
            TaskAttr waitForAttr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_WAITFOR);
            if(waitForAttr != null && !StringUtils.isBlank(waitForAttr.value)) {
                continue;
            }
            
            ret.add(task);
        }
        
        return ret;
    }
    
    public List<Task> getSortedTasks() {
        List<Task> ret = this.getIndividualTasks();
        
        final TaskSortChainItem chain = TaskAttrRuleManager.getInstance().getTaskSortChainItem();
        if(chain != null) {
            Collections.sort(ret, new Comparator<Task>(){
                @Override
                public int compare(Task o1, Task o2) {
                    SortResult ret = chain.sort(o1, o2);
                    return ret.result;
                }
            });
        }
        
        return ret;
    }
    
    public String sortOut(Task t1, Task t2) {
        if(t1 == null || t2 == null) {
            return null;
        }
        
        TaskSortChainItem chain = TaskAttrRuleManager.getInstance().getTaskSortChainItem();
        SortResult ret = chain.sort(t1, t2);
        return ret.reason;
    }
    
    @SuppressWarnings("unchecked")
    public List<TaskInfo> getDueTodos(List<Task> sortedTasks, TimeZone tz) {
        if(sortedTasks == null) {
            return Collections.EMPTY_LIST;
        }
        
        List<Task> all = sortedTasks;
        
        List<TaskInfo> filtered = new ArrayList<TaskInfo>();
        for(Task task : all) {
            TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_DUE_AT);
            if(attr != null) {
                if(attr.value != null) {
                    TaskInfo info = new TaskInfo();
                    info.task = task;
                    info.dueTime = attr.value;
                    if(tz != null) {
                        Date dueDate = TimeUtils.parseDateTime(info.dueTime, tz);
                        info.remainTime = TimeUtils.getRemainingTime(dueDate.getTime());
                    }
                    filtered.add(info);
                }
            }
        }
        
        int limit = ConfigManager.getInstance().getConfigAsInt("todo.view.limit", 10);
        if(filtered.size() > limit) {
            return filtered.subList(0, limit);
        }
        else {
            return filtered;
        }
    }
    
    private boolean acceptInTodos(Task task) {
        if(task == null) {
            return false;
        }
        
        Context currentContext = ContextManager.getInstance().getCurrentContext();
        
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_CONTEXT);
        if(currentContext != null) {
            if(attr != null) {
                if(attr.value != null && !attr.value.equals(String.valueOf(currentContext.id))) {
                    return false;
                }
            }
        }
        
        attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_DUE_AT);
        if(attr != null && !StringUtils.isBlank(attr.value)) {
            return false;
        }
        
        return true;
    }
    
    public List<Task> getTodos(List<Task> sortedTasks) {
        return this.getTodos(sortedTasks, false);
    }
    
    @SuppressWarnings("unchecked")
    public List<Task> getTodos(List<Task> sortedTasks, boolean showAll) {
        if(sortedTasks == null) {
            return Collections.EMPTY_LIST;
        }
        
        List<Task> all = sortedTasks;
        
        
        List<Task> selected = new ArrayList<Task>();
        for(Task task : all) {
            if(this.acceptInTodos(task)) {
                selected.add(task);
            }
        }
        all = selected;
        
        if(showAll) {
            return all;
        }
        else {
            int limit = ConfigManager.getInstance().getConfigAsInt("todo.view.limit", 10);
            if(all.size() > limit) {
                return all.subList(0, limit);
            }
            else {
                return all;
            }
        }
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
    
    public boolean isValidProject(Task task) {
        if(task == null) {
            return false;
        }
        
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_PARENT);
        if(attr != null) {
            if(this.projects.containsKey(task.id)) {
                return false;
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
    
    public String getTypeHint(Task task) {
        if(task == null) {
            return null;
        }
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_TYPE);
        return this.getTypeHint(attr);
    }
    
    public String getContextHint(Task task) {
        if(task == null) {
            return null;
        }
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_CONTEXT);
        return this.getContextHint(attr);
    }
    
    public String getPlanHint(Task task) {
        if(task == null) {
            return null;
        }
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_PLAN);
        return this.getPlanHint(attr);
    }
    
    public String getSeedHint(Task task) {
        if(task == null) {
            return null;
        }
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_SEED);
        return this.getSeedHint(attr);
    }
    
    public String getFollowerHint(Task task) {
        if(task == null) {
            return null;
        }
        TaskAttr attr = this.getTaskAttr(task, TaskAttrDefManager.DEF_INTERACTOR);
        return this.getFollowerHint(attr);
    }
    
    public String getTypeHint(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        try {
            if(!StringUtils.isBlank(attr.value)) {
                String type = attr.value;
                StringBuffer sb = new StringBuffer();
                sb.append("<img src='");
                sb.append(URLManager.getInstance().getBaseUrl());
                sb.append("/images/task/");
                sb.append(type);
                sb.append(".png' alt='");
                sb.append(type);
                sb.append("' data-toggle='tooltip' title='");
                sb.append(type);
                sb.append("'/>");
                return sb.toString();
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public String getContextHint(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        try {
            Context context = ContextManager.getInstance().getContext(Integer.parseInt(attr.value));
            return "@<span style='color:" + context.color + "'>" + context.name + "</span>"; 
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public String getPlanHint(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        try {
            Plan plan = PlanManager.getInstance().getPlan(Integer.parseInt(attr.value));
            return "~<span style='color:Violet'>" + plan.name + "</span>"; 
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public String getSeedHint(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        if(StringUtils.isBlank(attr.value)) {
            return null;
        }
        else {
            return "&<span style='color:ForestGreen'>" + attr.value + "</span>";
        }
    }
    
    public String getFollowerHint(TaskAttr attr) {
        if(attr == null) {
            return null;
        }
        if(StringUtils.isBlank(attr.value)) {
            return null;
        }
        else {
            return "$<span style='color:FireBrick'>" + attr.value + "</span>";
        }
    }
    
    public Map<Integer, Set<Integer>> getDependency() {
        return this.dep;
    }
    
    public Map<Integer, TaskDepNode> getTaskDepNodes() {
        Set<Integer> ids = new HashSet<Integer>();
        for(Entry<Integer, Set<Integer>> entry : this.dep.entrySet()) {
            ids.add(entry.getKey());
            ids.addAll(entry.getValue());
        }
        Map<Integer, TaskDepNode> ret = new HashMap<Integer, TaskDepNode>();
        for(int id : ids) {
            Task task = this.getTask(id);
            if(task != null) {
                TaskDepNode node = new TaskDepNode();
                node.id = String.valueOf(id);
                node.name = task.name;
                ret.put(id, node);
            }
        }
        return ret;
    }
    
    public Map<Integer, TaskProjectNode> getTaskProjectNodes() {
        Set<Integer> ids = new HashSet<Integer>();
        for(Entry<Integer, Set<Integer>> entry : this.projects.entrySet()) {
            ids.add(entry.getKey());
            ids.addAll(entry.getValue());
        }
        Map<Integer, TaskProjectNode> ret = new HashMap<Integer, TaskProjectNode>();
        for(int id : ids) {
            Task task = this.getTask(id);
            if(task != null) {
                TaskProjectNode node = new TaskProjectNode();
                node.id = String.valueOf(id);
                node.name = task.name;
                ret.put(id, node);
            }
        }
        return ret;
    }
    
    public List<TaskDepEdge> getTaskDepEdges(Map<Integer, TaskDepNode> nodes) {
        List<TaskDepEdge> ret = new ArrayList<TaskDepEdge>();
        for(Entry<Integer, Set<Integer>> entry : this.dep.entrySet()) {
            int from_id = entry.getKey();
            for(int to_id : entry.getValue()) {
                TaskDepEdge edge = new TaskDepEdge();
                edge.id = String.valueOf(from_id) + "-" + String.valueOf(to_id);
                edge.source = nodes.get(from_id);
                edge.target = nodes.get(to_id);
                if(edge.source == null || edge.target == null) {
                    continue;
                }
                ret.add(edge);
            }
        }
        return ret;
    }
    
    public List<TaskProjectEdge> getTaskProjectEdges(Map<Integer, TaskProjectNode> nodes) {
        List<TaskProjectEdge> ret = new ArrayList<TaskProjectEdge>();
        for(Entry<Integer, Set<Integer>> entry : this.projects.entrySet()) {
            int from_id = entry.getKey();
            for(int to_id : entry.getValue()) {
                TaskProjectEdge edge = new TaskProjectEdge();
                edge.id = String.valueOf(from_id) + "-" + String.valueOf(to_id);
                edge.source = nodes.get(from_id);
                edge.target = nodes.get(to_id);
                if(edge.source == null || edge.target == null) {
                    continue;
                }
                ret.add(edge);
            }
        }
        return ret;
    }
    
    public Task randomTask() {
        List<Task> tasks = this.getTasks();
        if(tasks == null || tasks.isEmpty()) {
            return null;
        }
        int n = DiceManager.getInstance().random(tasks.size());
        return tasks.get(n);
    }
    
    public List<Task> getRelatedTasks(Task task) {
        if(task == null) {
            return Collections.emptyList();
        }
        
        List<Task> ret = new ArrayList<Task>();
        
        String name = task.name;
        String [] items = name.split(" ");
        for(String item : items) {
            item = item.trim().toLowerCase();
            if(StringUtils.isBlank(item)) {
                continue;
            }
            Set<String> aliases = AliasManager.getInstance().getAliases(item);
            for(String alias : aliases) {
                Set<Task> tasks = this.tagCache.get(alias);
                if(tasks != null) {
                    for(Task t : tasks) {
                        if(!ret.contains(t)) {
                            ret.add(t);
                        }
                    }
                }
            }
        }
        
        TaskTag tag = TaskTagManager.getInstance().getTaskTagByTaskId(task.id);
        if(tag != null) {
            String tags = tag.tags;
            if(!StringUtils.isBlank(tags)) {
                for(String item : tags.split(",")) {
                    item = item.trim();
                    Set<TaskTag> tts = TaskTagManager.getInstance().getTaskTagsByTag(item);
                    if(tts != null) {
                        for(TaskTag tt : tts) {
                            Task t = this.getTask(tt.taskId);
                            if(t != null) {
                                if(!ret.contains(t)) {
                                    ret.add(t);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        List<Task> relatedTos = this.getRelatedToTasks(task);
        for(Task relatedTo : relatedTos) {
            if(!ret.contains(relatedTo)) {
                ret.add(relatedTo);
            }
        }
        
        //exclude itself
        Task self = null;
        for(Task t : ret) {
            if(t.id == task.id) {
                self = t;
                break;
            }
        }
        if(self != null) {
            ret.remove(self);
        }
        
        return ret;
    }
    
    private List<Task> getRelatedToTasks(Task task) {
        List<Task> ret = new ArrayList<Task>();
        
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_RELATED_TO);
        if(attr != null) {
            try {
                int to_id = Integer.parseInt(attr.value);
                Task to_task = this.getTask(to_id);
                if(to_task != null) {
                    ret.add(to_task);
                }
            }
            catch(Exception e) {
            }
        }
        
        for(Task t : this.getTasks()) {
            if(t.id != task.id) {
                attr = TaskAttr.getTaskAttr(t.attrs, TaskAttrDefManager.DEF_RELATED_TO);
                if(attr != null) {
                    try {
                        int to_id = Integer.parseInt(attr.value);
                        if(to_id == task.id) {
                            ret.add(t);
                        }
                    }
                    catch(Exception e) {
                    }
                }
            }
        }
        
        return ret;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Task task = (Task)target;
        return task.name;
    }
    
    public boolean isProject(Task task) {
        if(task == null) {
            return false;
        }
        
        return this.projects.containsKey(task.id);
    }
    
    public boolean isChildTask(Task task) {
        if(task == null) {
            return false;
        }
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_PARENT);
        if(attr != null && !StringUtils.isBlank(attr.value)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public List<Task> getWaitingTasks() {
        List<Task> ret = new ArrayList<Task>();
        
        for(Task task : this.getTasks()) {
            TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_WAITFOR);
            if(attr != null && !StringUtils.isBlank(attr.value)) {
                if(this.acceptInTodos(task)) {
                    ret.add(task);
                }
            }
        }
        
        return ret;
    }
    
    /**
     * Get directly dependent tasks
     * 
     * @return
     */
    public List<Task> getDependentTasks(Task task) {
        List<Task> ret = new ArrayList<Task>();
        
        if(task != null) {
            Set<Integer> ids = this.dep.get(task.id);
            if(ids != null) {
                for(Integer id : ids) {
                    Task t = this.getTask(id);
                    if(t != null) {
                        ret.add(t);
                    }
                }
            }
        }
        
        return ret;
    }
    
    /**
     * Get directly depended tasks
     * 
     * @return
     */
    public List<Task> getDependedTasks(Task task) {
        List<Task> ret = new ArrayList<Task>();
        
        if(task != null) {
            for(Entry<Integer, Set<Integer>> entry : this.dep.entrySet()) {
                int id = entry.getKey();
                Set<Integer> ids = entry.getValue();
                if(ids != null && ids.contains(task.id)) {
                    Task t = this.getTask(id);
                    if(t != null) {
                        ret.add(t);
                    }
                }
            }
        }
        
        return ret;
    }
    
    /**
     * Get the foremost dependent tasks
     * 
     * @return
     */
    public List<Task> getBlockingTasks(Task task) {
        List<Task> ret = new ArrayList<Task>();
        
        if(task != null) {
            Set<Integer> ids = this.getDependentTaskIds(task.id);
            if(ids != null) {
                for(Integer id : ids) {
                    Set<Integer> set = this.dep.get(id);
                    if(set == null || set.isEmpty()) {
                        Task t = this.getTask(id);
                        if(t != null) {
                            ret.add(t);
                        }
                    }
                }
            }
        }
        
        return ret;
    }
    
    public String getTaskDisplay(Task task) {
        if(task == null) {
            return null;
        }
        
        String starredStr = "";
        if(StarManager.getInstance().isStarred(task)) {
            starredStr = "<span class='glyphicon glyphicon-star' aria-hidden='true'></span>";
        }
        String contextStr = this.getContextHint(task);
        if(contextStr == null) {
            contextStr = "";
        }
        String seedStr = this.getSeedHint(task);
        if(seedStr == null) {
            seedStr = "";
        }
        String followerStr = this.getFollowerHint(task);
        if(followerStr == null) {
            followerStr = "";
        }
        String typeStr = this.getTypeHint(task);
        if(typeStr == null) {
            typeStr = "";
        }
        String planStr = this.getPlanHint(task);
        if(planStr == null) {
            planStr = "";
        }

        return typeStr + " " + starredStr + " " + task.name + " " + contextStr + " " + seedStr + " " + followerStr + " " + planStr; 
    }
    
    public Task getParentTask(Task task) {
        if(task == null) {
            return null;
        }
        
        try {
            int id = Integer.parseInt(task.getValue(TaskAttrDefManager.DEF_PARENT));
            Task parent = this.getTask(id);
            return parent;
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public List<Task> getChildrenTasks(Task task) {
        List<Task> ret = new ArrayList<Task>();
        
        if(task != null) {
            Set<Integer> ids = this.projects.get(task.id);
            if(ids != null) {
                for(Integer id : ids) {
                    Task t = this.getTask(id);
                    if(t != null) {
                        ret.add(t);
                    }
                }
            }
        }
        
        return ret;
    }
    
    public Document getReferredDocument(Task task) {
        if(task == null) {
            return null;
        }
        
        TaskAttr attr = TaskAttr.getTaskAttr(task.attrs, TaskAttrDefManager.DEF_REFER_TO);
        if(attr != null) {
            if(!StringUtils.isBlank(attr.value)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Document doc = DocumentManager.getInstance().getDocument(id);
                    return doc;
                }
                catch(Exception e) {
                }
            }
        }
        
        return null;
    }
    
    public Task getLastCreatedTask() {
        List<Task> tasks = this.getTasks();
        if(tasks.isEmpty()) {
            return null;
        }
        
        Collections.sort(tasks, new Comparator<Task>(){

            @Override
            public int compare(Task o1, Task o2) {
                return -(o1.id - o2.id);
            }
            
        });
        
        return tasks.get(0);
    }
    
    public Map<String, Double> getTaskTypeStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        int total = ConfigManager.getInstance().getConfigAsInt("task.num.limit", 50);
        int active = this.getIndividualTasks().size();
        if(active > total) {
            total = active;
        }
        int free = total - active;
        
        double active_pct = FormatUtils.getRoundedValue(active * 100.0 / total);
        double free_pct = FormatUtils.getRoundedValue(free * 100.0 / total);
        
        ret.put("Active", active_pct);
        ret.put("Free", free_pct);
        
        return ret;
    }
    
    public Map<String, Double> getTaskAttrContextStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        List<Task> tasks = this.getTasks();
        int total = tasks.size();
        Map<String, Integer> counts = new HashMap<String, Integer>();
        String missingContext = "Missing Context";
        for(Task task : tasks) {
            String value = task.getRealValue(TaskAttrDefManager.DEF_CONTEXT);
            String context = missingContext;
            if(!StringUtils.isBlank(value)) {
                context = value;
            }
            Integer i = counts.get(context);
            if(i == null) {
                i = 0;
            }
            i += 1;
            counts.put(context, i);
        }
        
        for(Entry<String, Integer> entry : counts.entrySet()) {
            double pct = FormatUtils.getRoundedValue(entry.getValue() * 100.0 / total);
            ret.put(entry.getKey(), pct);
        }
        
        return ret;
    }
    
    public Map<String, Double> getTaskAttrTypeStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        List<Task> tasks = this.getTasks();
        int total = tasks.size();
        Map<String, Integer> counts = new HashMap<String, Integer>();
        String missingType = "Missing Type";
        for(Task task : tasks) {
            String value = task.getRealValue(TaskAttrDefManager.DEF_TYPE);
            String context = missingType;
            if(!StringUtils.isBlank(value)) {
                context = value;
            }
            Integer i = counts.get(context);
            if(i == null) {
                i = 0;
            }
            i += 1;
            counts.put(context, i);
        }
        
        for(Entry<String, Integer> entry : counts.entrySet()) {
            double pct = FormatUtils.getRoundedValue(entry.getValue() * 100.0 / total);
            ret.put(entry.getKey(), pct);
        }
        
        return ret;
    }
    
    public Map<String, Double> getTaskEventStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        Map<String, Integer> all = StatsManager.getInstance().getEventTypeStats();
        Map<String, Integer> data = new HashMap<String, Integer>();
        for(Entry<String, Integer> entry : all.entrySet()) {
            String type = entry.getKey();
            if(type.contains("Task")) {
                data.put(type, entry.getValue());
            }
        }
        
        int sum = 0;
        for(Integer i : data.values()) {
            sum += i;
        }
        
        if(sum != 0) {
            for(Entry<String, Integer> entry : data.entrySet()) {
                String type = entry.getKey();
                double pct = FormatUtils.getRoundedValue(entry.getValue() * 100.0 / sum);
                ret.put(type, pct);
            }
        }
        
        return ret;
    }
    
    public long getNextReviewTime(Task task) {
        if(task == null) {
            return -1;
        }
        
        int days = ConfigManager.getInstance().getConfigAsInt("task.review.default.days", 7);
        int max_days = ConfigManager.getInstance().getConfigAsInt("task.review.max.days", 30);
        int min_days = ConfigManager.getInstance().getConfigAsInt("task.review.min.days", 1);
        
        int priority = 50;
        int urgency = 50;
        try {
            priority = Integer.parseInt(task.getRealValue(TaskAttrDefManager.DEF_PRIORITY));
            urgency = Integer.parseInt(task.getRealValue(TaskAttrDefManager.DEF_URGENCY));
        }
        catch(Exception e) {
        }
        
        double priority_ratio = 1.0 - (priority - 50) * 1.0 / 50;
        double urgency_ratio = 1.0 - (urgency - 50) * 1.0 / 50;
        
        long time = (long) (days * TimeUtils.DAY_DURATION * priority_ratio * urgency_ratio);
        long max_time = max_days * TimeUtils.DAY_DURATION;
        long min_time = min_days * TimeUtils.DAY_DURATION;
        if(time > max_time) {
            time = max_time;
        }
        if(time < min_time) {
            time = min_time;
        }
        
        return time + task.modifiedTime;
    }
    
    public List<Task> getTasksToReview() {
        List<Task> ret = new ArrayList<Task>();
        
        long now = System.currentTimeMillis();
        for(Task task : this.getIndividualTasks()) {
            long time = this.getNextReviewTime(task);
            if(time < now) {
                ret.add(task);
            }
        }
        
        return ret;
    }
    
    public List<Task> getOutdoorTasks() {
        List<Task> ret = new ArrayList<Task>();
        
        for(Task task : this.getIndividualTasks()) {
            String outdoor = task.getRealValue(TaskAttrDefManager.DEF_OUTDOOR);
            if(!StringUtils.isBlank(outdoor)) {
                ret.add(task);
            }
        }
        
        return ret;
    }
    
    public String getRelatedLink(Task task) {
        if(task == null) {
            return "javascript:void(0)";
        }
        
        String val = task.getValue(TaskAttrDefManager.DEF_MENU);
        String ret = null;
        if(!StringUtils.isBlank(val)) {
            MenuItem item = MenuManager.getInstance().getMenuItem(val);
            if(item != null) {
                ret = item.link;
            }
        }
        
        if(ret == null) {
            ret = task.getValue(TaskAttrDefManager.DEF_URL);
        }
        
        if(ret == null) {
            return "javascript:void(0)";
        }
        else {
            return ret;
        }
    }
}
