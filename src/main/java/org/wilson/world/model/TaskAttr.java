package org.wilson.world.model;

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
}
