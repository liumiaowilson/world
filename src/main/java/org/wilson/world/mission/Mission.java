package org.wilson.world.mission;

import java.util.HashMap;
import java.util.Map;

public class Mission {
    public int id;
    
    public String name;
    
    public MissionStatus status;
    
    public Map<String, Integer> target = new HashMap<String, Integer>();
    
    public Map<String, Integer> current = new HashMap<String, Integer>();
    
    public MissionReward reward;
    
    public boolean recommended;
    
    public MissionType type;
}
