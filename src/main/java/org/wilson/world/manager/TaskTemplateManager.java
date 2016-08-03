package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.TaskTemplateInfo;
import org.wilson.world.task.ActionTaskTemplate;
import org.wilson.world.task.TaskTemplate;
import org.wilson.world.task.TaskTemplateFactory;

public class TaskTemplateManager implements ManagerLifecycle{
    private static TaskTemplateManager instance;
    
    private Cache<String, TaskTemplate> cache = null;
    private Cache<Integer, TaskTemplateInfo> infoCache = null;
    
    private TaskTemplateManager() {
        this.cache = new DefaultCache<String, TaskTemplate>("task_template_manager_cache", false);
        this.infoCache = new DefaultCache<Integer, TaskTemplateInfo>("task_template_manager_info_cache", false);
    }
    
    private void loadTaskTemplates() {
        for(TaskTemplate template : TaskTemplateFactory.getInstance().getTaskTemplates()) {
            this.loadTaskTemplate(template);
        }
        
        String templates = ConfigManager.getInstance().getConfig("task.templates");
        if(templates != null) {
            String [] items = templates.split(",");
            for(String item : items) {
                this.loadTaskTemplate(new ActionTaskTemplate(item));
            }
        }
    }
    
    private void loadTaskTemplate(TaskTemplate template) {
        if(template != null) {
            this.cache.put(template.getName(), template);
            
            TaskTemplateInfo info = new TaskTemplateInfo();
            info.id = this.cache.getAll().size();
            info.name = template.getName();
            info.attrs = template.getTemplateAttributes();
            this.infoCache.put(info.id, info);
        }
    }
    
    public static TaskTemplateManager getInstance() {
        if(instance == null) {
            instance = new TaskTemplateManager();
        }
        return instance;
    }

    @Override
    public void start() {
        this.loadTaskTemplates();
    }

    @Override
    public void shutdown() {
    }
    
    public TaskTemplate getTaskTemplate(String name) {
        return this.cache.get(name);
    }
    
    public List<TaskTemplate> getTaskTemplates() {
        return this.cache.getAll();
    }
    
    public TaskTemplateInfo getTaskTemplateInfo(int id) {
        return this.infoCache.get(id);
    }
    
    public List<TaskTemplateInfo> getTaskTemplateInfos() {
        return this.infoCache.getAll();
    }
}
