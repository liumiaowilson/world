package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class ExpManager implements EventListener{
    private static final Logger logger = Logger.getLogger(ExpManager.class);
    
    private static ExpManager instance;
    
    private Map<EventType, Integer> points = new HashMap<EventType, Integer>();
    
    private Map<Integer, Integer> levelCache = new HashMap<Integer, Integer>();
    
    private ExpManager() {
        this.reload();
        
        EventManager.getInstance().registerListener(EventType.ConfigOverrideUploaded, this);
        
        for(EventType type : points.keySet()) {
            EventManager.getInstance().registerListener(type, this);
        }
    }
    
    private void reload() {
        String hiddenTypes = ConfigManager.getInstance().getConfig("point.events.hidden");
        List<EventType> hiddenList = new ArrayList<EventType>();
        if(hiddenTypes != null) {
            String [] items = hiddenTypes.trim().split(",");
            for(String item : items) {
                EventType type = this.getEventType(item);
                if(type != null) {
                    if(!hiddenList.contains(type)) {
                        hiddenList.add(type);
                    }
                }
            }
        }
        
        int defaultPoint = ConfigManager.getInstance().getConfigAsInt("point.all", 1);
        
        List<EventType> all = new ArrayList<EventType>();
        for(EventType type : EventType.values()) {
            if(!hiddenList.contains(type)) {
                all.add(type);
            }
        }
        
        for(EventType type : all) {
            String key = "point." + type.toString();
            int point = ConfigManager.getInstance().getConfigAsInt(key, defaultPoint);
            this.points.put(type, point);
        }
    }
    
    private EventType getEventType(String name) {
        try {
            EventType type = EventType.valueOf(name);
            return type;
        }
        catch(Exception e) {
            logger.error("failed to get event type", e);
            return null;
        }
    }
    
    public static ExpManager getInstance() {
        if(instance == null) {
            instance = new ExpManager();
        }
        return instance;
    }
    
    public int getExp() {
        return DataManager.getInstance().getValueAsInt("user.exp");
    }
    
    public void setExp(int value) {
        DataManager.getInstance().setValue("user.exp", value);
    }
    
    public int getLevelExp(int level) {
        Integer result = this.levelCache.get(level);
        if(result == null) {
            String formula = ConfigManager.getInstance().getConfig("user.level.exp", "x * x");
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("x", level);
            Object obj = ScriptManager.getInstance().run(formula, context);
            int ret = 0;
            try {
                ret = (int) Double.parseDouble(String.valueOf(obj));
            }
            catch(Exception e) {
            }
            
            this.levelCache.put(level, ret);
            result = ret;
        }
        return result;
    }
    
    public int getNextLevelExp() {
        int level = getLevel();
        int exp = getExp();
        int next_level_exp = this.getLevelExp(level + 1);
        return next_level_exp - exp;
    }
    
    public int getLevel() {
        int exp = this.getExp();
        int level = 0;
        while(true) {
            if(exp < getLevelExp(level)) {
                break;
            }
            level = level + 1;
        }
        return level - 1;
    }
    
    public int getCurrentLevelExperiencePercentage() {
        int level = getLevel();
        int exp = getExp();
        int current_level_exp = this.getLevelExp(level);
        int next_level_exp = this.getLevelExp(level + 1);
        int all = next_level_exp - current_level_exp;
        int pass = exp - current_level_exp;
        int pct = (int) (pass * 100.0 / all);
        return pct;
    }
    
    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void handle(Event event) {
        if(EventType.ConfigOverrideUploaded == event.type) {
            this.reload();
        }
        else {
            int exp = this.getExp();
            int point = this.points.get(event.type);
            exp = exp + point;
            this.setExp(exp);
        }
    }
}
