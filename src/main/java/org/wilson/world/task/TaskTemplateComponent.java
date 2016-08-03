package org.wilson.world.task;

import java.util.List;

import org.wilson.world.model.TaskAttr;

public interface TaskTemplateComponent {
    public String getName();
    
    public List<TaskAttr> getTemplateAttributes();
    
    public List<TaskTemplateComponent> next();
    
    public List<TaskTemplate> getTaskTemplates();
}
