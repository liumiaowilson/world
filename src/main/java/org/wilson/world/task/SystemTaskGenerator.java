package org.wilson.world.task;

import java.util.Date;
import java.util.TimeZone;

import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.model.Task;
import org.wilson.world.util.TimeUtils;

public abstract class SystemTaskGenerator implements TaskGenerator {
    private long lastRunTime = -1;
    private static final String SUFFIX_LAST_RUN = "_tg";
    
    public String getSystemName() {
        String name = this.getName();
        int len = 20 - SUFFIX_LAST_RUN.length();
        if(name.length() > len) {
            name = name.substring(0, len);
        }
        return name;
    }
    
    private long getLastRunTime() {
        return DataManager.getInstance().getValueAsLong(this.getSystemName() + SUFFIX_LAST_RUN);
    }
    
    private void setLastRunTime(long time) {
        DataManager.getInstance().setValue(this.getSystemName() + SUFFIX_LAST_RUN, time);
    }

    @Override
    public boolean canStart(TimeZone tz, Date date) {
        this.lastRunTime = this.getLastRunTime();
        if(this.lastRunTime < 0) {
            return true;
        }
        
        int days = ConfigManager.getInstance().getConfigAsInt("task_generator." + this.getSystemName() + ".interval.days", 1);
        long time = days * TimeUtils.DAY_DURATION + this.lastRunTime;
        return date.getTime() > time;
    }

    @Override
    public Task generateTask() {

        this.lastRunTime = System.currentTimeMillis();
        this.setLastRunTime(this.lastRunTime);
        
        return this.spawn();
    }

    public abstract Task spawn();
}
