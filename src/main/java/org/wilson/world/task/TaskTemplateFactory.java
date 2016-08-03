package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

public class TaskTemplateFactory {
    private static TaskTemplateFactory instance;
    
    private List<TaskTemplate> templates = new ArrayList<TaskTemplate>();
    
    private TaskTemplateFactory() {
        this.loadTaskTemplates();
    }
    
    private void loadTaskTemplates() {
        TaskTemplateComponent root = new BaseTaskTemplateComponent();
        this.templates.addAll(root.getTaskTemplates());
    }
    
    public static TaskTemplateFactory getInstance() {
        if(instance == null) {
            instance = new TaskTemplateFactory();
        }
        return instance;
    }
    
    public List<TaskTemplate> getTaskTemplates() {
        return this.templates;
    }
}
