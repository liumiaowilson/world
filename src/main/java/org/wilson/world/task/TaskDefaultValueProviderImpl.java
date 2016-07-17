package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrDefManager;

public class TaskDefaultValueProviderImpl implements TaskDefaultValueProvider {

    @Override
    public String getDefaultValue(String name) {
        if(name == null) {
            return null;
        }
        if(TaskAttrDefManager.DEF_EFFORT.equals(name)) {
            return "2";
        }
        else if(TaskAttrDefManager.DEF_PRIORITY.equals(name)) {
            return "50";
        }
        else if(TaskAttrDefManager.DEF_URGENCY.equals(name)) {
            return "50";
        }
        else {
            return null;
        }
    }

}
