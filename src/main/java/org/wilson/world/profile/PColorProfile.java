package org.wilson.world.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PColorProfile {
    public static final String [] TYPES = {
            "Red",
            "Blue",
            "Yellow",
            "Green"
    };
    
    public int red;
    
    public int blue;
    
    public int yellow;
    
    public int green;
    
    private List<String> types = new ArrayList<String>();
    
    public void init() {
        List<Info> infos = new ArrayList<Info>();
        infos.add(new Info(red, TYPES[0]));
        infos.add(new Info(blue, TYPES[1]));
        infos.add(new Info(yellow, TYPES[2]));
        infos.add(new Info(green, TYPES[3]));
        
        Collections.sort(infos, new Comparator<Info>(){

            @Override
            public int compare(Info o1, Info o2) {
                return -Integer.compare(o1.val, o2.val);
            }
            
        });
        
        Info dominant = infos.get(0);
        if(dominant.val > 15) {
            this.types.add(dominant.name);
        }
        else {
            Info subDominant = infos.get(1);
            this.types.add(dominant.name);
            this.types.add(subDominant.name);
        }
    }
    
    public List<String> getTypes() {
        return Collections.unmodifiableList(this.types);
    }
    
    public String getTypeDisplay() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.types.size(); i++) {
            sb.append(this.types.get(i));
            if(i != this.types.size() - 1) {
                sb.append(",");
            }
        }
        
        return sb.toString();
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
