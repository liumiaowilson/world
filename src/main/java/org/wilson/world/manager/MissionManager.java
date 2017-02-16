package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.mission.AddMissionJob;
import org.wilson.world.mission.DefaultMissionEventProvider;
import org.wilson.world.mission.DelMissionJob;
import org.wilson.world.mission.Mission;
import org.wilson.world.mission.MissionEventProvider;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;
import org.wilson.world.mission.MissionStatus;
import org.wilson.world.mission.MissionTodayContentProvider;
import org.wilson.world.mission.MissionType;
import org.wilson.world.util.NameGenerator;

public class MissionManager implements ManagerLifecycle, EventListener {
    private static final Logger logger = Logger.getLogger(MissionManager.class);
    
    private static MissionManager instance;
    
    private List<MissionRewardGenerator> rewardRenerators = new ArrayList<MissionRewardGenerator>();
    
    private Map<Integer, Mission> missions = new HashMap<Integer, Mission>();
    
    private static int GLOBAL_ID = 1;
    
    private NameGenerator nameGenerator = new NameGenerator();
    
    private boolean initialized = false;
    
    private MissionEventProvider missionEventProvider = new DefaultMissionEventProvider();
    
    private Map<String, Integer> events = new HashMap<String, Integer>();
    
    private MissionManager() {
        for(EventType type : EventType.values()) {
            EventManager.getInstance().registerListener(type, this);
        }
        
        ScheduleManager.getInstance().addJob(new AddMissionJob());
        ScheduleManager.getInstance().addJob(new DelMissionJob());
        
        TodayManager.getInstance().addTodayContentProvider(new MissionTodayContentProvider());
    }
    
    public static MissionManager getInstance() {
        if(instance == null) {
            instance = new MissionManager();
        }
        
        return instance;
    }
    
    private void loadMissions() {
        GLOBAL_ID = 1;
        
        int size = this.getMissionDefaultSize();
        Map<String, Integer> data = StatsManager.getInstance().getEventTypeStats();
        
        int topn = ConfigManager.getInstance().getConfigAsInt("mission.event.top.size", 10);
        List<String> topEvents = StatsManager.getInstance().getTopEvents(topn);
        for(int i = 0; i < size; i++) {
            Mission mission = this.generateMission(data, topEvents);
            //avoid loops
            this.addMission(mission, false);
        }
        
        this.initialized = true;
    }
    
    public int getMissionDefaultSize() {
        return ConfigManager.getInstance().getConfigAsInt("mission.default.size", 20);
    }
    
    public List<Mission> getMissions() {
        return new ArrayList<Mission>(this.missions.values());
    }
    
    public List<Mission> getRecommendedMissions() {
        List<Mission> ret = new ArrayList<Mission>();
        
        for(Mission mission : this.getMissions()) {
            if(mission.recommended) {
                ret.add(mission);
            }
        }
        
        return ret;
    }
    
    private void addMission(Mission mission, boolean needInitialized) {
        if(needInitialized) {
            if(!this.initialized) {
                this.loadMissions();
            }
        }
        
        if(mission != null && !mission.target.isEmpty()) {
            mission.id = GLOBAL_ID++;
            this.missions.put(mission.id, mission);
        }
    }
    
    public void addMission(Mission mission) {
        this.addMission(mission, true);
    }
    
    public void removeMission(Mission mission) {
        if(mission != null) {
            this.missions.remove(mission.id);
        }
    }
    
    public Mission generateMission(Map<String, Integer> data, List<String> topEvents) {
        return this.generateMission(data, topEvents, this.missionEventProvider);
    }
    
    public Mission generateMission(Map<String, Integer> data, List<String> topEvents, MissionEventProvider provider) {
        if(data == null) {
            data = StatsManager.getInstance().getEventTypeStats();
        }
        if(topEvents == null) {
            topEvents = StatsManager.getInstance().getTopEvents(ConfigManager.getInstance().getConfigAsInt("mission.event.top.size", 10), data);
        }
        
        Mission mission = new Mission();
        
        mission.name = this.nameGenerator.getName();
        mission.status = MissionStatus.NORMAL;
        mission.type = MissionType.Default;
        mission.time = System.currentTimeMillis();
        
        MissionReward reward = this.generateMissionReward();
        if(reward == null) {
            return null;
        }
        mission.reward = reward;
        
        int worth = reward.getWorth();
        
        int most = 0;
        for(int pct : data.values()) {
            if(pct > most) {
                most = pct;
            }
        }
        
        if(most == 0) {
        	return null;
        }
        
        int base = 1;
        int val = 0;
        int max_value = ConfigManager.getInstance().getConfigAsInt("mission.event.max_value", 20);
        int step = 0;
        while(val < worth && step < 20) {
            String type = provider.nextEventTypeName(data);
            if(type == null) {
            	step++;
                continue;
            }
            Integer count = data.get(type);
            if(count == null) {
                count = provider.getEventTypeDefaultValue(type);
            }
            int added = max_value;
            if(count != 0) {
                added = (int) (base * most / count);
                if(added > max_value) {
                    added = max_value;
                }
            }
            
            val += added;
            Integer i = mission.target.get(type);
            if(i == null) {
                i = 0;
            }
            i += 1;
            mission.target.put(type, i);
            
            step++;
        }
        
        boolean recommended = true;
        for(String type : mission.target.keySet()) {
            if(!topEvents.contains(type)) {
                recommended = false;
            }
        }
        mission.recommended = recommended;
        
        return mission;
    }
    
    public Mission generateMission() {
        return this.generateMission(null, null);
    }
    
    public void addMissionRewardGenerator(MissionRewardGenerator provider) {
        if(provider != null) {
            this.rewardRenerators.add(provider);
        }
    }
    
    public void removeMissionRewardGenerator(MissionRewardGenerator provider) {
        if(provider != null) {
            this.rewardRenerators.remove(provider);
        }
    }
    
    public MissionReward generateMissionReward() {
        if(this.rewardRenerators.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(this.rewardRenerators.size());
        return this.rewardRenerators.get(n).generate();
    }

    @Override
    public void start() {
        logger.info("Loading missions...");
        
        if(!this.initialized) {
            this.loadMissions();
        }
    }

    @Override
    public void shutdown() {
    }
    
    public String getContent(Mission mission) {
        StringBuffer sb = new StringBuffer();
        
        if(mission == null) {
            return sb.toString();
        }
        
        if(MissionStatus.NORMAL == mission.status) {
            Map<String, Integer> target = mission.target;
            if(target != null) {
                for(Entry<String, Integer> entry : target.entrySet()) {
                	String key = entry.getKey();
                	int value = entry.getValue();
                	int bucketNum = 0;
                	if(this.events.containsKey(key)) {
                		bucketNum = this.events.get(key);
                	}
                    sb.append("[" + EventManager.getInstance().getEventTypeDisplay(key) + "] x " + value);
                    if(bucketNum > 0) {
                    	sb.append("(" + bucketNum + ")");
                    }
                    sb.append(" ");
                }
            }
        }
        else {
            Map<String, Integer> target = mission.target;
            Map<String, Integer> current = mission.current;
            
            sb.append("<table class=\"table table-striped table-bordered\"><thead><tr><th>Name</th><th>Amount</th></tr></thead><tbody>");
            List<String> keys = new ArrayList<String>(target.keySet());
            Collections.sort(keys);
            for(String key : keys) {
                int target_amount = target.get(key);
                Integer current_amount = current.get(key);
                if(current_amount == null) {
                    current_amount = 0;
                }
                
                boolean passed = current_amount >= target_amount;
                sb.append("<tr><td>" + EventManager.getInstance().getEventTypeDisplay(key) + "</td><td><span style=\"color: " + (passed ? "green" : "red") + "\">" + current_amount + "</span>/" + target_amount + "</td></tr>");
            }
            sb.append("</tbody></table>");
        }
        
        return sb.toString();
    }
    
    public Mission getMission(int id) {
        return this.missions.get(id);
    }
    
    public Mission getAcceptedMission() {
        for(Mission mission : this.missions.values()) {
            if(MissionStatus.ACCEPTED == mission.status) {
                return mission;
            }
        }
        
        return null;
    }
    
    public String acceptMission(int id) {
        Mission mission = this.getMission(id);
        if(mission == null) {
            return "Mission cannot be found.";
        }
        
        Mission accepted = this.getAcceptedMission();
        if(accepted != null) {
            return "Cannot accept mission as mission [" + accepted.name + "] has already been accepted.";
        }
        
        mission.status = MissionStatus.ACCEPTED;
        
        return null;
    }
    
    public String abandonMission(int id) {
        Mission mission = this.getMission(id);
        if(mission == null) {
            return "Mission cannot be found.";
        }
        
        mission.status = MissionStatus.NORMAL;
        mission.current.clear();
        
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
    
    public boolean isMissionComplete(Mission mission) {
        if(mission == null) {
            return false;
        }
        
        boolean isComplete = true;
        
        for(Entry<String, Integer> entry : mission.target.entrySet()) {
            String key = entry.getKey();
            int target_amount = entry.getValue();
            Integer current_amount = mission.current.get(key);
            if(current_amount == null) {
                current_amount = 0;
            }
            if(current_amount < target_amount) {
                isComplete = false;
                break;
            }
        }
        
        return isComplete;
    }
    
    private void trackEvent(Event event) {
    	Integer count = this.events.get(event.type.name());
    	if(count == null) {
    		count = 0;
    	}
    	count += 1;
    	this.events.put(event.type.name(), count);
    }

    public boolean isMissionAutowireEnabled() {
    	return ConfigManager.getInstance().getConfigAsBoolean("mission.autowire.enabled", true);
    }
    
    private boolean canBeAutowired(Mission mission) {
    	if(mission == null) {
    		return false;
    	}
    	
    	for(Entry<String, Integer> entry : mission.target.entrySet()) {
    		String name = entry.getKey();
    		int expectedCount = entry.getValue();
    		if(!this.events.containsKey(name)) {
    			return false;
    		}
    		int count = this.events.get(name);
    		if(count < expectedCount) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    private void autowire(Mission mission) {
    	if(mission == null) {
    		return;
    	}
    	
    	if(mission.reward != null) {
    		mission.reward.deliver();
    	}
    	
    	this.missions.remove(mission.id);
    	
    	for(Entry<String, Integer> entry : mission.target.entrySet()) {
    		String name = entry.getKey();
    		int expectedCount = entry.getValue();
    		int count = this.events.get(name);
    		count -= expectedCount;
    		this.events.put(name, count);
    	}
    }
    
    @Override
    public void handle(Event event) {
    	this.trackEvent(event);
    	
        Mission active = this.getAcceptedMission();
        if(active != null) {
            String name = event.type.name();
            if(active.target.containsKey(name)) {
                Integer count = active.current.get(name);
                if(count == null) {
                    count = 0;
                }
                count += 1;
                active.current.put(name, count);
                
                if(this.isMissionComplete(active)) {
                    active.reward.deliver();
                    
                    this.missions.remove(active.id);
                }
            }
        }
        else {
        	if(this.isMissionAutowireEnabled()) {
        		for(Mission mission : this.getMissions()) {
        			if(this.canBeAutowired(mission)) {
        				this.autowire(mission);
        			}
        		}
        	}
        }
    }
    
    public List<Mission> getMissionsOfType(MissionType type) {
        if(type == null) {
            return Collections.emptyList();
        }
        
        List<Mission> ret = new ArrayList<Mission>();
        
        for(Mission mission : this.getMissions()) {
            if(mission.type == type) {
                ret.add(mission);
            }
        }
        
        return ret;
    }
    
    public Map<String, Integer> getEventBuckets() {
    	return this.events;
    }
}
