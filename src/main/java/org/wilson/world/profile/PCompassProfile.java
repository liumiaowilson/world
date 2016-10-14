package org.wilson.world.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PCompassProfile {
    public static final String [] TYPES = {
            "North",
            "South",
            "West",
            "East",
    };
    
    public int north;
    
    public int south;
    
    public int west;
    
    public int east;
    
    private String dominantType;
    
    private String subDominantType;
    
    public void init() {
        List<Info> infos = new ArrayList<Info>();
        infos.add(new Info(north, TYPES[0]));
        infos.add(new Info(south, TYPES[1]));
        infos.add(new Info(west, TYPES[2]));
        infos.add(new Info(east, TYPES[3]));
        Collections.sort(infos, new Comparator<Info>(){

            @Override
            public int compare(Info o1, Info o2) {
                return -Integer.compare(o1.val, o2.val);
            }
            
        });
        
        this.dominantType = infos.get(0).name;
        this.subDominantType = infos.get(1).name;
    }
    
    public String getDominantType() {
        return this.dominantType;
    }
    
    public String getSubDominantType() {
        return this.subDominantType;
    }
    
    public static class Info {
        public int val;
        
        public String name;
        
        public Info(int val, String name) {
            this.val = val;
            this.name = name;
        }
    }
}
