package org.wilson.world.dress;

public class SeasonEnergy {
    public int id;
    
    public String name;
    
    public String [] keywords;
    
    public String form;
    
    public String line;
    
    public String energy;
    
    public String color;
    
    public String texture;
    
    public String contrast;
    
    public String colors;
    
    public String size;
    
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < keywords.length; i++) {
            sb.append(keywords[i]);
            if(i != keywords.length - 1) {
                sb.append(",");
            }
        }
        
        return sb.toString();
    }
}
