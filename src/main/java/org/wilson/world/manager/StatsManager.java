package org.wilson.world.manager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.wilson.world.dao.DAO;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.dao.StatsItemDAO.StatsItemQueryAllTemplate;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.model.StatsItem;
import org.wilson.world.stats.PurgeStatsJob;
import org.wilson.world.util.FormatUtils;

public class StatsManager implements EventListener {
    private static StatsManager instance;
    
    private DAO<StatsItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private StatsManager() {
        this.dao = DAOManager.getInstance().getDAO(StatsItem.class);
        
        for(EventType type : EventType.values()) {
            EventManager.getInstance().registerListener(type, this);
        }
        
        ScheduleManager.getInstance().addJob(new PurgeStatsJob());
    }
    
    public static StatsManager getInstance() {
        if(instance == null) {
            instance = new StatsManager();
        }
        return instance;
    }
    
    public void log(EventType eventType) {
        if(eventType == null) {
            return;
        }
        String type = eventType.toString();
        long time = System.currentTimeMillis();
        
        StatsItem item = new StatsItem();
        item.type = type;
        item.time = time;
        
        this.dao.create(item);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        this.log(event.type);
    }
    
    public Map<String, Integer> getEventTypeStats() {
        Map<String, Integer> all = new HashMap<String, Integer>();
        long current = System.currentTimeMillis();
        long last = current - 30 * 24 * 60 * 60 * 1000L;
        QueryTemplate<StatsItem> qt = this.dao.getQueryTemplate(StatsItemQueryAllTemplate.NAME);
        List<StatsItem> items = this.dao.query(qt, last, current);
        for(StatsItem item : items) {
            String type = item.type;
            Integer value = all.get(type);
            if(value == null) {
                value = 0;
            }
            value = value + 1;
            all.put(type, value);
        }
        
        return all;
    }
    
    public Map<String, Double> getEventTypesInOneMonth() {
        Map<String, Double> ret = new HashMap<String, Double>();
        Map<String, Integer> all = this.getEventTypeStats();
        
        int sum = 0;
        for(Integer i : all.values()) {
            sum += i;
        }
        if(sum != 0) {
            for(Entry<String, Integer> entry : all.entrySet()) {
                String type = entry.getKey();
                int count = entry.getValue();
                double pct = count * 100.0 / sum;
                pct = FormatUtils.getRoundedValue(pct);
                ret.put(type, pct);
            }
        }
        return ret;
    }
    
    public Map<Long, TrendInfo> getTrendInOneMonth(TimeZone tz) {
        Map<Long, TrendInfo> ret = new HashMap<Long, TrendInfo>();
        long current = System.currentTimeMillis();
        long last = current - 30 * 24 * 60 * 60 * 1000L;
        QueryTemplate<StatsItem> qt = this.dao.getQueryTemplate(StatsItemQueryAllTemplate.NAME);
        List<StatsItem> items = this.dao.query(qt, last, current);
        for(StatsItem item : items) {
            long time = item.time;
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(tz);
            cal.setTimeInMillis(time);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DATE);
            String timeStr = "Date.UTC(" + year + "," + month + "," + day + ")";
            time = cal.getTimeInMillis();
            
            TrendInfo info = ret.get(time);
            if(info == null) {
                info = new TrendInfo();
                info.time = time;
                info.timeStr = timeStr;
            }
            info.count = info.count + 1;
            ret.put(time, info);
        }
        
        return ret;
    }
    
    public static class TrendInfo {
        public long time;
        public String timeStr;
        public int count;
    }
}
