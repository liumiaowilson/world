package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}
