package org.wilson.world.profile;

public class SmalleyProfile {
    public static final String [] TYPES = new String [] {
            "Lion",
            "Otter",
            "Golden Retriever",
            "Beaver"
    };
    
    public int lion;
    
    public int otter;
    
    public int goldenRetriever;
    
    public int beaver;
    
    public String getType() {
        int [] vals = new int [4];
        vals[0] = lion;
        vals[1] = otter;
        vals[2] = goldenRetriever;
        vals[3] = beaver;
        
        int max = 0;
        for(int i = 0; i < 4; i++) {
            if(vals[i] > max) {
                max = vals[i];
            }
        }
        
        int index = 0;
        for(int i = 0; i < 4; i++) {
            if(vals[i] == max) {
                index = i;
                break;
            }
        }
        
        return TYPES[index];
    }
}
