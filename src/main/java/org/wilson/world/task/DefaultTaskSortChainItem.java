package org.wilson.world.task;

import org.wilson.world.manager.TaskAttrRuleManager;
import org.wilson.world.manager.TaskManager;
import org.wilson.world.model.Task;
import org.wilson.world.model.TaskAttr;

public class DefaultTaskSortChainItem implements TaskSortChainItem {
    private String name;
    private TaskSortChainItem next;
    
    public DefaultTaskSortChainItem(String name, TaskSortChainItem next) {
        this.name = name;
        this.next = next;
    }
    
    public void setNext(TaskSortChainItem next) {
        this.next = next;
    }
    
    @Override
    public TaskSortChainItem next() {
        return next;
    }

    @Override
    public SortResult sort(Task task1, Task task2) {
        TaskAttr attr1 = TaskManager.getInstance().getTaskAttr(task1, name);
        TaskAttr attr2 = TaskManager.getInstance().getTaskAttr(task2, name);
        TaskAttrComparator comparator = TaskAttrRuleManager.getInstance().getTaskAttrComparator(name);
        if(comparator == null) {
            if(this.next != null) {
                return this.next.sort(task1, task2);
            }
        }
        else {
            int ret = comparator.compare(attr1, attr2);
            if(ret == 0) {
                if(this.next != null) {
                    return this.next.sort(task1, task2);
                }
            }
            else {
                String value1 = attr1 == null ? null : attr1.value;
                String value2 = attr2 == null ? null : attr2.value;
                
                value1 = value1 == null ? "" : value1;
                value2 = value2 == null ? "" : value2;
                
                return SortResult.create(name + " [" + value1 + " : " + value2 + "]", ret);
            }
        }
        
        return SortResult.create("", 0);
    }

}
