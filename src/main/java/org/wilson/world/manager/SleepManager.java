package org.wilson.world.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Sleep;
import org.wilson.world.sleep.PurgeSleepJob;
import org.wilson.world.sleep.SleepInfo;
import org.wilson.world.sleep.SleepRewardEventListener;
import org.wilson.world.util.FormatUtils;
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
        
        EventManager.getInstance().registerListener(EventType.CreateSleep, new SleepRewardEventListener());
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
    
    public List<SleepInfo> getSleepInfos(TimeZone tz) throws ParseException {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        List<SleepInfo> ret = new ArrayList<SleepInfo>();
        
        Map<String, List<Sleep>> data = new HashMap<String, List<Sleep>>();
        for(Sleep sleep : this.getSleeps()) {
            long endTime = sleep.endTime;
            String timeStr = TimeUtils.toDateString(endTime, tz);
            List<Sleep> sleeps = data.get(timeStr);
            if(sleeps == null) {
                sleeps = new ArrayList<Sleep>();
                data.put(timeStr, sleeps);
            }
            sleeps.add(sleep);
        }
        
        for(Entry<String, List<Sleep>> entry : data.entrySet()) {
            String timeStr = entry.getKey();
            List<Sleep> sleeps = entry.getValue();
            long time = TimeUtils.fromDateString(timeStr, tz).getTime();
            long total_time = 0;
            int total_quality = 0;
            int total_dreams = 0;
            for(Sleep sleep : sleeps) {
                total_time += sleep.endTime - sleep.startTime;
                total_quality += sleep.quality;
                total_dreams += sleep.dreams;
            }
            
            SleepInfo info = new SleepInfo();
            info.time = time;
            info.timeStr = timeStr;
            info.hours = FormatUtils.getRoundedValue(1.0 * total_time / TimeUtils.HOUR_DURATION);
            info.quality = FormatUtils.getRoundedValue(1.0 * total_quality / sleeps.size());
            info.dreams = FormatUtils.getRoundedValue(1.0 * total_dreams);
            ret.add(info);
        }
        
        Collections.sort(ret, new Comparator<SleepInfo>(){

            @Override
            public int compare(SleepInfo o1, SleepInfo o2) {
                if(o1.time < o2.time) {
                    return -1;
                }
                else if(o1.time > o2.time) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
            
        });
        
        return ret;
    }
}
