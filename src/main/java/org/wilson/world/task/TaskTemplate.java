package org.wilson.world.task;

import java.util.List;

import org.wilson.world.model.TaskAttr;

public interface TaskTemplate {
    public String getName();
    
    public List<TaskAttr> getTemplateAttributes();
}
