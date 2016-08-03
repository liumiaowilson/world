package org.wilson.world.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.model.TaskAttr;

public abstract class AbstractTaskTemplateComponent implements TaskTemplateComponent {
    private List<TaskAttr> attrs = new ArrayList<TaskAttr>();
    private List<TaskTemplateComponent> next = new ArrayList<TaskTemplateComponent>();
    
    public void addTaskAttr(TaskAttr attr) {
        this.attrs.add(attr);
    }
    
    public void addTaskTemplateComponent(TaskTemplateComponent component) {
        this.next.add(component);
    }
    
    @Override
    public List<TaskAttr> getTemplateAttributes() {
        return this.attrs;
    }

    @Override
    public List<TaskTemplateComponent> next() {
        return this.next;
    }

    @Override
    public List<TaskTemplate> getTaskTemplates() {
        List<TaskTemplate> ret = new ArrayList<TaskTemplate>();
        
        if(this.next().isEmpty()) {
            DefaultTaskTemplate template = new DefaultTaskTemplate(this.getName(), this.attrs);
            ret.add(template);
        }
        else {
            for(TaskTemplateComponent component : this.next()) {
                List<TaskTemplate> templates = component.getTaskTemplates();
                if(templates != null) {
                    for(TaskTemplate template : templates) {
                        if(template instanceof DefaultTaskTemplate) {
                            DefaultTaskTemplate dtt = (DefaultTaskTemplate)template;
                            if(!StringUtils.isBlank(this.getName())) {
                                String name = dtt.getName();
                                name = this.getName() + "_" + name;
                                dtt.setName(name);
                            }
                            
                            List<TaskAttr> attrs = dtt.getTemplateAttributes();
                            for(TaskAttr attr : this.getTemplateAttributes()) {
                                if(TaskAttr.getTaskAttr(attrs, attr.name) == null) {
                                    attrs.add(attr);
                                }
                            }
                            
                            ret.add(dtt);
                        }
                    }
                }
            }
        }
        
        return ret;
    }
}
