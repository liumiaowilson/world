package org.wilson.world.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.wilson.world.manager.TaskAttrDefManager;
import org.wilson.world.model.TaskAttr;

public class DefaultTaskAttrComparator implements TaskAttrComparator {
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean reversed = false;
    
    public DefaultTaskAttrComparator(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public int compare(TaskAttr attr1, TaskAttr attr2) {
        if(attr1 == null && attr2 == null) {
            return 0;
        }
        else if(attr1 == null && attr2 != null) {
            return 1;
        }
        else if(attr1 != null && attr2 == null) {
            return -1;
        }
        else {
            if(!attr1.name.equals(attr2.name)) {
                return 0;
            }
            String type = TaskAttrDefManager.getInstance().getTaskAttrType(attr1.name);
            return this.compare(attr1.value, attr2.value, type);
        }
    }

    private int compare(String value1, String value2, String type) {
        if(value1 == null && value2 == null) {
            return 0;
        }
        else if(value1 != null && value2 == null) {
            return -1;
        }
        else if(value1 == null && value2 != null) {
            return 1;
        }
        else {
            int ret = this.doCompare(value1, value2, type);
            if(this.reversed) {
                return -ret;
            }
            else {
                return ret;
            }
        }
    }
    
    private int doCompare(String value1, String value2, String type) {
        if(TaskAttrDefManager.TYPE_BOOLEAN.equals(type)) {
            return this.compareBoolean(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_INTEGER.equals(type)) {
            return this.compareInteger(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_LONG.equals(type)) {
            return this.compareLong(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_DOUBLE.equals(type)) {
            return this.compareDouble(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_STRING.equals(type)) {
            return this.compareString(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_DATE.equals(type)) {
            return this.compareDate(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_DATETIME.equals(type)) {
            return this.compareDateTime(value1, value2);
        }
        else if(TaskAttrDefManager.TYPE_TASK.equals(type)) {
            return this.compareTask(value1, value2);
        }
        else {
            return 0;
        }
    }
    
    private int compareBoolean(String value1, String value2) {
        try {
            boolean b1 = Boolean.parseBoolean(value1);
            boolean b2 = Boolean.parseBoolean(value2);
            if(b1 == b2) {
                return 0;
            }
            else if(b1 && !b2) {
                return -1;
            }
            else {
                return 1;
            }
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    private int compareInteger(String value1, String value2) {
        try {
            int i1 = Integer.parseInt(value1);
            int i2 = Integer.parseInt(value2);
            return i1 - i2;
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    private int compareLong(String value1, String value2) {
        try {
            long l1 = Long.parseLong(value1);
            long l2 = Long.parseLong(value2);
            return (int) (l1 - l2);
        }
        catch(Exception e) {
            return 0;
        }
    }

    private int compareDouble(String value1, String value2) {
        try {
            double d1 = Double.parseDouble(value1);
            double d2 = Double.parseDouble(value2);
            return (int) (d1 - d2);
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    private int compareString(String value1, String value2) {
        return value1.compareTo(value2);
    }
    
    private int compareDate(String value1, String value2) {
        try {
            Date d1 = dateFormat.parse(value1);
            Date d2 = dateFormat.parse(value2);
            return (int) (d1.getTime() - d2.getTime());
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    private int compareDateTime(String value1, String value2) {
        try {
            Date d1 = datetimeFormat.parse(value1);
            Date d2 = datetimeFormat.parse(value2);
            return (int) (d1.getTime() - d2.getTime());
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    private int compareTask(String value1, String value2) {
        return 0;
    }
}
