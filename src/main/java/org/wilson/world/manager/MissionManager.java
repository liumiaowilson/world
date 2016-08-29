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
import org.wilson.world.mission.Mission;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;
import org.wilson.world.mission.MissionStatus;
import org.wilson.world.util.NameGenerator;

public class MissionManager implements ManagerLifecycle, EventListener {
    private static final Logger logger = Logger.getLogger(MissionManager.class);
    
    private static MissionManager instance;
    
    private List<MissionRewardGenerator> rewardRenerators = new ArrayList<MissionRewardGenerator>();
    
    private Map<Integer, Mission> missions = new HashMap<Integer, Mission>();
    
    private static int GLOBAL_ID = 1;
    
    private NameGenerator nameGenerator = new NameGenerator();
    
    private MissionManager() {
        for(EventType type : EventType.values()) {
            EventManager.getInstance().registerListener(type, this);
        }
        
        ScheduleManager.getInstance().addJob(new AddMissionJob());
    }
    
    public static MissionManager getInstance() {
        if(instance == null) {
            instance = new MissionManager();
        }
        
        return instance;
    }
    
    private void loadMissions() {
        GLOBAL_ID = 1;
        
        int size = ConfigManager.getInstance().getConfigAsInt("mission.default.size", 20);
        Map<String, Double> data = StatsManager.getInstance().getEventTypesInOneMonth();
        for(int i = 0; i < size; i++) {
            Mission mission = this.generateMission(data);
            this.addMission(mission);
        }
    }
    
    public List<Mission> getMissions() {
        return new ArrayList<Mission>(this.missions.values());
    }
    
    public void addMission(Mission mission) {
        if(mission != null && !mission.target.isEmpty()) {
            mission.id = GLOBAL_ID++;
            this.missions.put(mission.id, mission);
        }
    }
    
    private Mission generateMission(Map<String, Double> data) {
        if(data == null) {
            data = StatsManager.getInstance().getEventTypesInOneMonth();
        }
        
        Mission mission = new Mission();
        
        mission.name = this.nameGenerator.getName();
        mission.status = MissionStatus.NORMAL;
        
        MissionReward reward = this.generateMissionReward();
        if(reward == null) {
            return null;
        }
        mission.reward = reward;
        
        int worth = reward.getWorth();
        
        List<String> types = new ArrayList<String>(data.keySet());
        double most = 0;
        for(double pct : data.values()) {
            if(pct > most) {
                most = pct;
            }
        }
        
        int base = 1;
        int val = 0;
        int total = types.size();
        int max_value = ConfigManager.getInstance().getConfigAsInt("mission.event.max_value", 20);
        while(val < worth) {
            int n = DiceManager.getInstance().random(total);
            String type = types.get(n);
            double pct = data.get(type);
            int added = max_value;
            if(pct != 0) {
                added = (int) (base * most / pct);
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
        }
        
        return mission;
    }
    
    public Mission generateMission() {
        return this.generateMission(null);
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
        
        this.loadMissions();
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
                    sb.append("[" + entry.getKey() + "] x " + entry.getValue() + " ");
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
                sb.append("<tr><td>" + key + "</td><td><span style=\"color: " + (passed ? "green" : "red") + "\">" + current_amount + "</span>/" + target_amount + "</td></tr>");
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

    @Override
    public void handle(Event event) {
        Mission active = this.getAcceptedMission();
        if(active == null) {
            return;
        }
        
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
}
