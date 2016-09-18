package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.wilson.world.event.EventType;
import org.wilson.world.gym.GymItem;
import org.wilson.world.gym.GymMissionEventProvider;
import org.wilson.world.gym.GymMissionJob;
import org.wilson.world.gym.GymType;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.menu.MenuItem;
import org.wilson.world.mission.Mission;
import org.wilson.world.mission.MissionEventProvider;
import org.wilson.world.mission.MissionType;

import net.sf.json.JSONObject;

public class GymManager implements ManagerLifecycle {
    public static final String GYM_ITEM_KEY = "gym";
    
    private static final Logger logger = Logger.getLogger(GymManager.class);
    
    private static GymManager instance;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, GymItem> items = new HashMap<Integer, GymItem>();
    
    private Map<String, List<GymItem>> categories = new HashMap<String, List<GymItem>>();
    
    private MissionEventProvider missionEventProvider = new GymMissionEventProvider();
    
    private GymManager() {
        ScheduleManager.getInstance().addJob(new GymMissionJob());
    }
    
    public static GymManager getInstance() {
        if(instance == null) {
            instance = new GymManager();
        }
        
        return instance;
    }
    
    public void addGymItem(GymItem item) {
        if(item != null) {
            item.id = GLOBAL_ID++;
            this.items.put(item.id, item);
            
            List<GymItem> items = this.categories.get(item.type.name());
            if(items == null) {
                items = new ArrayList<GymItem>();
                this.categories.put(item.type.name(), items);
            }
            items.add(item);
        }
    }
    
    private void loadGymItems() {
        List<String> menuIds = MenuManager.getInstance().getSingleMenuIds();
        for(String menuId : menuIds) {
            MenuItem menu = MenuManager.getInstance().getMenuItem(menuId);
            if(menu.data.containsKey(GYM_ITEM_KEY)) {
                JSONObject data = (JSONObject) menu.data.get(GYM_ITEM_KEY);
                if(data != null) {
                    try {
                        String name = data.getString("name");
                        GymType type = GymType.valueOf(data.getString("type"));
                        EventType event = EventType.valueOf(data.getString("event"));
                        
                        GymItem item = new GymItem();
                        item.name = name;
                        item.type = type;
                        item.menu = menu;
                        item.event = event;
                        this.addGymItem(item);
                    }
                    catch(Exception e){
                        logger.error(e);
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        logger.info("Start to load gym items ...");
        this.loadGymItems();
        
        logger.info("Start to generate gym missions ...");
        this.generateGymMissions();
    }
    
    private void generateGymMissions() {
        int size = this.getGymMissionDefaultSize();
        Map<String, Integer> eventData = StatsManager.getInstance().getEventTypeStats();
        List<String> topEvents = StatsManager.getInstance().getTopEvents(ConfigManager.getInstance().getConfigAsInt("mission.event.top.size", 10), eventData);
        for(int i = 0; i < size; i++) {
            Mission mission = this.generateGymMission(eventData, topEvents);
            if(mission != null) {
                MissionManager.getInstance().addMission(mission);
            }
        }
    }
    
    public int getGymMissionDefaultSize() {
        return ConfigManager.getInstance().getConfigAsInt("gym.mission.default.size", 20);
    }
    
    public Mission generateGymMission() {
        return this.generateGymMission(null, null);
    }
    
    public GymItem randomGymItem(boolean useBioCurve) {
        if(this.items.isEmpty()) {
            return null;
        }
        
        if(useBioCurve) {
            TimeZone tz = ConfigManager.getInstance().getUserDefaultTimeZone();
            if(tz == null) {
                tz = TimeZone.getDefault();
            }
            try {
                int energy = HealthManager.getInstance().getCurrentEnergyPower(tz);
                if(energy < 0) {
                    energy = 0;
                }
                
                int emotion = HealthManager.getInstance().getCurrentEmotionPower(tz);
                if(emotion < 0) {
                    emotion = 0;
                }
                
                int intelligence = HealthManager.getInstance().getCurrentIntelligencePower(tz);
                if(intelligence < 0) {
                    intelligence = 0;
                }
                
                int sum = energy + emotion + intelligence;
                if(sum == 0) {
                    return this.randomGymItem();
                }
                
                int p_energy = energy * 100 / sum;
                int p_emotion = emotion * 100 / sum;
                
                int p = DiceManager.getInstance().random(100);
                if(p < p_energy) {
                    return this.randomGymItem(GymType.Energy);
                }
                else if(p < p_energy + p_emotion) {
                    return this.randomGymItem(GymType.EQ);
                }
                else {
                    return this.randomGymItem(GymType.IQ);
                }
            }
            catch(Exception e) {
                logger.error(e);
            }
            
            return null;
        }
        else {
            return this.randomGymItem();
        }
    }
    
    public GymItem randomGymItem(GymType type) {
        List<GymItem> items = this.categories.get(type.name());
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }
    
    public GymItem randomGymItem() {
        List<GymItem> items = this.getGymItems();
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }
    
    public Mission generateGymMission(Map<String, Integer> eventData, List<String> topEvents) {
        Mission mission = MissionManager.getInstance().generateMission(eventData, topEvents, this.missionEventProvider);
        mission.name = "Gym " + mission.name;
        mission.type = MissionType.Gym;
        return mission;
    }
    
    @Override
    public void shutdown() {
    }
    
    public List<GymItem> getGymItems() {
        return new ArrayList<GymItem>(this.items.values());
    }
    
    public List<GymItem> getGymItems(String type) {
        return this.categories.get(type);
    }
}
