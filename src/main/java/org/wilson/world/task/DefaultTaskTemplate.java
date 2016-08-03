package org.wilson.world.task;

import java.util.List;

import org.wilson.world.model.TaskAttr;

public class DefaultTaskTemplate implements TaskTemplate {
    private String name;
    private List<TaskAttr> templateAttributes;
    
    public DefaultTaskTemplate(String name, List<TaskAttr> attrs) {
        this.name = name;
        this.templateAttributes = attrs;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setTemplateAttributes(List<TaskAttr> templateAttributes) {
        this.templateAttributes = templateAttributes;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<TaskAttr> getTemplateAttributes() {
        return this.templateAttributes;
    }

}
