package org.wilson.world.model;

public class TaskTag {
    public int id;
    
    public int taskId;
    
    public String tags;
    
    public static TaskTag clone(TaskTag tag) {
        if(tag == null) {
            return null;
        }
        TaskTag newTag = new TaskTag();
        newTag.taskId = tag.taskId;
        newTag.tags = tag.tags;
        return newTag;
    }
}
