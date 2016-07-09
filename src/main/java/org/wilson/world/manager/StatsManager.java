package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class StatsManager implements EventListener {
    private static final Logger logger = Logger.getLogger(StatsManager.class);
    
    private static StatsManager instance;
    
    private StatsManager() {
        for(EventType type : EventType.values()) {
            EventManager.getInstance().registerListener(type, this);
        }
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
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into stats(type, time) values (?, ?);";
            ps = con.prepareStatement(sql);
            ps.setString(1, type);
            ps.setLong(2, time);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to log", e);
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        this.log(event.type);
    }
    
    public Map<String, Double> getEventTypesInOneMonth() {
        Map<String, Double> ret = new HashMap<String, Double>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> all = new HashMap<String, Integer>();
        try {
            con = DBUtils.getConnection();
            long current = System.currentTimeMillis();
            long last = current - 30 * 24 * 60 * 60 * 1000L;
            String sql = "select * from stats where time > ? and time < ?;";
            ps = con.prepareStatement(sql);
            ps.setLong(1, last);
            ps.setLong(2, current);
            rs = ps.executeQuery();
            String type = null;
            while(rs.next()) {
                type = rs.getString(2);
                Integer value = all.get(type);
                if(value == null) {
                    value = 0;
                }
                value = value + 1;
                all.put(type, value);
            }
        }
        catch(Exception e) {
            logger.error("failed to get event types in one month", e);
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
        
        int sum = 0;
        for(Integer i : all.values()) {
            sum += i;
        }
        if(sum != 0) {
            for(Entry<String, Integer> entry : all.entrySet()) {
                String type = entry.getKey();
                int count = entry.getValue();
                double pct = count * 100.0 / sum;
                ret.put(type, pct);
            }
        }
        return ret;
    }
}
