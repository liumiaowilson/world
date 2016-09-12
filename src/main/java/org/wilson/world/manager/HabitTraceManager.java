package org.wilson.world.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.EventType;
import org.wilson.world.habit.CheckHabitMonitor;
import org.wilson.world.habit.HabitCheckEventListener;
import org.wilson.world.habit.HabitTraceDBCleaner;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Habit;
import org.wilson.world.model.HabitTrace;

public class HabitTraceManager implements ItemTypeProvider {
    public static final String NAME = "habit_trace";
    
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static long DAY_DURATION = 24 * 60 * 60 * 1000L;
    
    private static HabitTraceManager instance;
    
    private DAO<HabitTrace> dao = null;
    private Cache<Integer, HabitTrace> cache = null;
    
    @SuppressWarnings("unchecked")
    private HabitTraceManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(HabitTrace.class);
        this.cache = new DefaultCache<Integer, HabitTrace>("habit_trace_manager_cache", false);
        ((CachedDAO<HabitTrace>)this.dao).getCache().addCacheListener(new CacheListener<HabitTrace>(){
            @Override
            public void cachePut(HabitTrace old, HabitTrace v) {
                if(old != null) {
                    HabitTraceManager.this.cache.delete(old.habitId);
                }
                HabitTraceManager.this.cache.put(v.habitId, v);
            }

            @Override
            public void cacheDeleted(HabitTrace v) {
                HabitTraceManager.this.cache.delete(v.habitId);
            }

            @Override
            public void cacheLoaded(List<HabitTrace> all) {
            }

            @Override
            public void cacheLoading(List<HabitTrace> old) {
                HabitTraceManager.this.cache.clear();
            }
        }); 
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        ItemManager.getInstance().addDBCleaner(new HabitTraceDBCleaner());
        
        EventManager.getInstance().registerListener(EventType.CheckHabit, new HabitCheckEventListener());
        
        MonitorManager.getInstance().registerMonitorParticipant(new CheckHabitMonitor());
    }
    
    public static HabitTraceManager getInstance() {
        if(instance == null) {
            instance = new HabitTraceManager();
        }
        return instance;
    }
    
    public void createHabitTrace(HabitTrace trace) {
        ItemManager.getInstance().checkDuplicate(trace);
        
        this.dao.create(trace);
    }
    
    public HabitTrace getHabitTrace(int id) {
        HabitTrace trace = this.dao.get(id);
        if(trace != null) {
            return trace;
        }
        else {
            return null;
        }
    }
    
    public List<HabitTrace> getHabitTraces() {
        List<HabitTrace> result = new ArrayList<HabitTrace>();
        for(HabitTrace trace : this.dao.getAll()) {
            result.add(trace);
        }
        return result;
    }
    
    public void updateHabitTrace(HabitTrace trace) {
        this.dao.update(trace);
    }
    
    public void deleteHabitTrace(int id) {
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
        return target instanceof HabitTrace;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        HabitTrace trace = (HabitTrace)target;
        return String.valueOf(trace.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public HabitTrace getHabitTraceByHabitId(int habitId) {
        return this.cache.get(habitId);
    }
    
    private String timeToString(Date date) {
        return format.format(date);
    }
    
    private Date stringToTime(String str) {
        try {
            return format.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public boolean isHabitCheckable(Habit habit, TimeZone tz) {
        if(habit == null) {
            return false;
        }
        HabitTrace trace = this.getHabitTraceByHabitId(habit.id);
        if(trace == null) {
            return true;
        }
        else {
            format.setTimeZone(tz);
            int interval = habit.interval;
            String lastStr = this.timeToString(new Date(trace.time));
            Date lastDate = this.stringToTime(lastStr);
            long next = lastDate.getTime() + DAY_DURATION * interval;
            long now = System.currentTimeMillis();
            if(now < next) {
                return false;
            }
            else {
                return true;
            }
        }
    }
    
    public String getLastTime(int habitId, TimeZone tz) {
        Habit habit = HabitManager.getInstance().getHabit(habitId);
        if(habit == null) {
            return null;
        }
        HabitTrace trace = this.getHabitTraceByHabitId(habitId);
        if(trace == null) {
            return null;
        }
        else {
            format.setTimeZone(tz);
            return this.timeToString(new Date(trace.time));
        }
    }
    
    public String getNextTime(int habitId, TimeZone tz) {
        Habit habit = HabitManager.getInstance().getHabit(habitId);
        if(habit == null) {
            return null;
        }
        HabitTrace trace = this.getHabitTraceByHabitId(habitId);
        if(trace == null) {
            return this.timeToString(new Date());
        }
        else {
            format.setTimeZone(tz);
            int interval = habit.interval;
            String lastStr = this.timeToString(new Date(trace.time));
            Date lastDate = this.stringToTime(lastStr);
            long next = lastDate.getTime() + DAY_DURATION * interval;
            return this.timeToString(new Date(next));
        }
    }
    
    public HabitTrace checkHabit(int habitId, TimeZone tz) {
        Habit habit = HabitManager.getInstance().getHabit(habitId);
        if(habit == null) {
            return null;
        }
        HabitTrace trace = this.getHabitTraceByHabitId(habitId);
        if(trace == null) {
            trace = new HabitTrace();
            trace.habitId = habitId;
            trace.maxStreak = 1;
            trace.streak = 1;
            trace.time = System.currentTimeMillis();
            this.createHabitTrace(trace);
        }
        else {
            format.setTimeZone(tz);
            int interval = habit.interval;
            String lastStr = this.timeToString(new Date(trace.time));
            Date lastDate = this.stringToTime(lastStr);
            long next = lastDate.getTime() + DAY_DURATION * interval;
            long now = System.currentTimeMillis();
            if(now < next) {
                return null;
            }
            else {
                long pass = next + DAY_DURATION * interval;
                if(now > pass) {
                    //no streak
                    trace.streak = 1;
                }
                else {
                    //continue streak
                    trace.streak += 1;
                    if(trace.streak > trace.maxStreak) {
                        trace.maxStreak = trace.streak;
                    }
                }
                trace.time = now;
                this.updateHabitTrace(trace);
            }
        }
        
        return trace;
    }
    
    @Override
    public String getIdentifier(Object target) {
        return null;
    }
}
