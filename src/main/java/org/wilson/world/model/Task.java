package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.world.manager.TaskAttrManager;
import org.wilson.world.manager.TaskManager;

public class Task {
    public int id;
    
    public String name;
    
    public String content;
    
    public long createdTime;
    
    public long modifiedTime;
    
    public List<TaskAttr> attrs = new ArrayList<TaskAttr>();
    
    public TaskTag tag;
    
    /**
     * Used for UI
     */
    public boolean marked;
    
    public boolean starred;
    
    public String seed;
    
    public String follower;
    
    public String context;
    
    public String type;
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "[" + id + "]");
        sb.append(":");
        for(int i = 0; i < attrs.size(); i++) {
            TaskAttr attr = attrs.get(i);
            sb.append(attr.name + "=" + attr.value);
            if(i != attrs.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    public String getRealValue(String attrName) {
        TaskAttr attr = TaskManager.getInstance().getTaskAttr(this, attrName);
        if(attr != null) {
            return (String) TaskAttrManager.getInstance().getRealValue(attr);
        }
        
        return null;
    }
    
    public String getValue(String attrName) {
        TaskAttr attr = TaskManager.getInstance().getTaskAttr(this, attrName);
        if(attr != null) {
            return attr.value;
        }
        
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Task) {
            Task t = (Task)obj;
            return t.id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
    
    public static Task newTask(String name, String content) {
        Task task = new Task();
        task.name = name;
        task.content = content;
        long createdTime = System.currentTimeMillis();
        task.createdTime = createdTime;
        task.modifiedTime = createdTime;
        
        List<TaskAttr> attrs = new ArrayList<TaskAttr>();
        Map<String, String> defaultValues = TaskManager.getInstance().getTaskAttrDefaultValues();
        if(defaultValues != null) {
            for(Entry<String, String> entry : defaultValues.entrySet()) {
                TaskAttr attr = new TaskAttr();
                attr.name = entry.getKey();
                attr.value = entry.getValue();
                attrs.add(attr);
            }
        }
        task.attrs = attrs;
        
        return task;
    }
}
