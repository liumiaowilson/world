package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.mission.Mission;
import org.wilson.world.mission.MissionReward;
import org.wilson.world.mission.MissionRewardGenerator;
import org.wilson.world.mission.MissionStatus;
import org.wilson.world.util.NameGenerator;

public class MissionManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(MissionManager.class);
    
    private static MissionManager instance;
    
    private List<MissionRewardGenerator> rewardRenerators = new ArrayList<MissionRewardGenerator>();
    
    private List<Mission> missions = new ArrayList<Mission>();
    
    private static int GLOBAL_ID = 1;
    
    private NameGenerator nameGenerator = new NameGenerator();
    
    private MissionManager() {
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
        for(int i = 0; i < size; i++) {
            Mission mission = this.generateMission();
            this.addMission(mission);
        }
    }
    
    public List<Mission> getMissions() {
        return this.missions;
    }
    
    public void addMission(Mission mission) {
        if(mission != null) {
            mission.id = GLOBAL_ID++;
            this.missions.add(mission);
        }
    }
    
    public Mission generateMission() {
        Mission mission = new Mission();
        
        mission.name = this.nameGenerator.getName();
        mission.status = MissionStatus.NORMAL;
        
        MissionReward reward = this.generateMissionReward();
        if(reward == null) {
            return null;
        }
        mission.reward = reward;
        
        int worth = reward.getWorth();
        Map<String, Double> data = StatsManager.getInstance().getEventTypesInOneMonth();
        if(data == null) {
            return null;
        }
        
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
    
    public String toString(Map<String, Integer> target) {
        StringBuffer sb = new StringBuffer();
        
        if(target != null) {
            for(Entry<String, Integer> entry : target.entrySet()) {
                sb.append("[" + entry.getKey() + "] x " + entry.getValue() + " ");
            }
        }
        
        return sb.toString();
    }
}
