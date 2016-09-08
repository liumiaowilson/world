package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Sleep;
import org.wilson.world.sleep.PurgeSleepJob;
import org.wilson.world.util.TimeUtils;

public class SleepManager implements ItemTypeProvider {
    public static final String NAME = "sleep";
    
    private static SleepManager instance;
    
    private DAO<Sleep> dao = null;
    
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
}
