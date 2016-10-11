package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.wilson.world.stats.EventRecord;
import org.wilson.world.stats.EventTypeInfo;
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
    
    public List<EventTypeInfo> getEventTypeInfos() {
        Map<String, Integer> map = this.getEventTypeStats();
        
        return this.getEventTypeInfos(map);
    }
    
    public List<EventTypeInfo> getEventTypeInfos(Map<String, Integer> data) {
        List<EventTypeInfo> ret = new ArrayList<EventTypeInfo>();
        
        for(Entry<String, Integer> entry : data.entrySet()) {
            EventTypeInfo info = new EventTypeInfo();
            info.name = entry.getKey();
            info.count = entry.getValue();
            ret.add(info);
        }
        
        return ret;
    }
    
    public List<String> getTopEvents(int num) {
        Map<String, Integer> data = this.getEventTypeStats();
        
        return this.getTopEvents(num, data);
    }
    
    public List<String> getTopEvents(int num, Map<String, Integer> data) {
        List<EventTypeInfo> infos = this.getEventTypeInfos(data);
        Collections.sort(infos, new Comparator<EventTypeInfo>(){

            @Override
            public int compare(EventTypeInfo o1, EventTypeInfo o2) {
                return -(o1.count - o2.count);
            }
            
        });
        
        List<String> ret = new ArrayList<String>();
        
        for(int i = 0; i < infos.size() && i < num; i++) {
            ret.add(infos.get(i).name);
        }
        
        return ret;
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
    
    public List<EventRecord> getEventRecords(TimeZone tz) {
        long current = System.currentTimeMillis();
        long last = current - 30 * 24 * 60 * 60 * 1000L;
        QueryTemplate<StatsItem> qt = this.dao.getQueryTemplate(StatsItemQueryAllTemplate.NAME);
        List<StatsItem> items = this.dao.query(qt, last, current);
        
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        Map<String, Map<Long, Integer>> hourMap = new HashMap<String, Map<Long, Integer>>();
        Map<String, Map<Long, Integer>> dayMap = new HashMap<String, Map<Long, Integer>>();
        
        for(StatsItem item : items) {
            long time = item.time;
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(tz);
            cal.setTimeInMillis(time);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long hourTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            long dayTime = cal.getTimeInMillis();
            
            String name = item.type;
            
            Integer count = countMap.get(name);
            if(count == null) {
                count = 0;
            }
            count += 1;
            countMap.put(name, count);
            
            Map<Long, Integer> hourData = hourMap.get(name);
            if(hourData == null) {
                hourData = new HashMap<Long, Integer>();
                hourMap.put(name, hourData);
            }
            Integer hourCount = hourData.get(hourTime);
            if(hourCount == null) {
                hourCount = 0;
            }
            hourCount += 1;
            hourData.put(hourTime, hourCount);
            
            Map<Long, Integer> dayData = dayMap.get(name);
            if(dayData == null) {
                dayData = new HashMap<Long, Integer>();
                dayMap.put(name, dayData);
            }
            Integer dayCount = dayData.get(dayTime);
            if(dayCount == null) {
                dayCount = 0;
            }
            dayCount += 1;
            dayData.put(dayTime, dayCount);
        }
        
        List<EventRecord> ret = new ArrayList<EventRecord>();
        for(Entry<String, Integer> entry : countMap.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();
            
            Map<Long, Integer> hourData = hourMap.get(name);
            int maxPerHour = 0;
            for(Integer i : hourData.values()) {
                if(i > maxPerHour) {
                    maxPerHour = i;
                }
            }
            
            Map<Long, Integer> dayData = dayMap.get(name);
            int maxPerDay = 0;
            for(Integer i : dayData.values()) {
                if(i > maxPerDay) {
                    maxPerDay = i;
                }
            }
            
            EventRecord record = new EventRecord();
            record.name = name;
            record.count = count;
            record.maxPerHour = maxPerHour;
            record.maxPerDay = maxPerDay;
            ret.add(record);
        }
        
        return ret;
    }
    
    public static class TrendInfo {
        public long time;
        public String timeStr;
        public int count;
    }
}
