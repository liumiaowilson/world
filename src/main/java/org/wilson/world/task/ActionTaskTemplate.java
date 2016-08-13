package org.wilson.world.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wilson.world.manager.ExtManager;
import org.wilson.world.model.TaskAttr;

public class ActionTaskTemplate implements TaskTemplate {
    private String name;
    private TaskTemplateEP ep;
    
    public ActionTaskTemplate(String name) {
        this.name = name;
        this.ep = (TaskTemplateEP) ExtManager.getInstance().wrapAction(this.name, TaskTemplateEP.class);
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskAttr> getTemplateAttributes() {
        if(this.ep == null) {
            return Collections.EMPTY_LIST;
        }
        else {
            List<TaskAttr> attrs = this.ep.getTemplateAttributes();
            List<TaskAttr> ret = new ArrayList<TaskAttr>(attrs.size());
            for(TaskAttr attr : attrs) {
                ret.add(attr.clone());
            }
            
            return ret;
        }
    }

}
