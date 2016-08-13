package org.wilson.world.model;

import java.util.List;

public class TaskAttr {
    public int id;
    
    public int taskId;
    
    public String name;
    
    public String value;
    
    public static TaskAttr create(String name, String value) {
        TaskAttr attr = new TaskAttr();
        attr.name = name;
        attr.value = value;
        return attr;
    }

    public static TaskAttr getTaskAttr(List<TaskAttr> ret, String name) {
        for(TaskAttr attr : ret) {
            if(attr.name.equals(name)) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public TaskAttr clone() {
        TaskAttr attr = new TaskAttr();
        attr.taskId = taskId;
        attr.name = name;
        attr.value = value;
        return attr;
    }
}
