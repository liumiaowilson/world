package org.wilson.world.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Sleep;
import org.wilson.world.sleep.PurgeSleepJob;
import org.wilson.world.util.TimeUtils;

public class SleepManager implements ItemTypeProvider {
    public static final String NAME = "sleep";
    
    private static SleepManager instance;
    
    private DAO<Sleep> dao = null;
    
    private long startTime = -1;
    
    @SuppressWarnings("unchecked")
    private SleepManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Sleep.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        ScheduleManager.getInstance().addJob(new PurgeSleepJob());
    }
    
    public static SleepManager getInstance() {
        if(instance == null) {
            instance = new SleepManager();
        }
        return instance;
    }
    
    public void createSleep(Sleep sleep) {
        ItemManager.getInstance().checkDuplicate(sleep);
        
        this.dao.create(sleep);
    }
    
    public Sleep getSleep(int id) {
        Sleep sleep = this.dao.get(id);
        if(sleep != null) {
            return sleep;
        }
        else {
            return null;
        }
    }
    
    public Sleep loadSleep(Sleep sleep, TimeZone tz) {
        if(sleep == null) {
            return null;
        }
        if(tz == null) {
            tz = TimeZone.getDefault(); 
        }
        
        sleep.startTimeStr = TimeUtils.toDateTimeString(sleep.startTime, tz);
        sleep.endTimeStr = TimeUtils.toDateTimeString(sleep.endTime, tz);
        
        return sleep;
    }
    
    public List<Sleep> getSleeps() {
        List<Sleep> result = new ArrayList<Sleep>();
        for(Sleep sleep : this.dao.getAll()) {
            result.add(sleep);
        }
        return result;
    }
    
    public void updateSleep(Sleep sleep) {
        this.dao.update(sleep);
    }
    
    public void deleteSleep(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Sleep;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Sleep sleep = (Sleep)target;
        return String.valueOf(sleep.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Sleep sleep = (Sleep)target;
        return String.valueOf(sleep.id);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public boolean isInSleep() {
        return this.getStartTime() > 0;
    }
    
    public String startSleep() {
        if(this.isInSleep()) {
            return "Already in sleep";
        }
        
        this.setStartTime(System.currentTimeMillis());
        return null;
    }
    
    public String endSleep(int quality, int dreams) {
        if(!this.isInSleep()) {
            return "Not in sleep";
        }
        
        long startTime = this.getStartTime();
        this.setStartTime(-1);
        long endTime = System.currentTimeMillis();
        Sleep sleep = new Sleep();
        sleep.startTime = startTime;
        sleep.endTime = endTime;
        sleep.quality = quality;
        sleep.dreams = dreams;
        this.createSleep(sleep);
        
        return null;
    }
    
    public Sleep getLastCreatedSleep() {
        List<Sleep> sleeps = this.getSleeps();
        if(sleeps.isEmpty()) {
            return null;
        }
        
        Collections.sort(sleeps, new Comparator<Sleep>(){

            @Override
            public int compare(Sleep o1, Sleep o2) {
                return -(o1.id - o2.id);
            }
            
        });
        
        return sleeps.get(0);
    }
    
    public String getStartTimeDisplay(String format, TimeZone tz) {
        if(StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        long startTime = this.getStartTime();
        Date startDate = new Date(startTime);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(tz);
        return sdf.format(startDate);
    }
}
