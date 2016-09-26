package org.wilson.world.dress;

import org.wilson.world.manager.DressManager;

public class DressColorFamily {
    public int id;
    
    public String name;
    
    public String spring;
    
    public String summer;
    
    public String autumn;
    
    public String winter;
    
    public String clear;
    
    public String soft;
    
    public String icyWarm;
    
    public String icyCool;
    
    public String display(String color) {
        if(color == null) {
            return "";
        }
        
        DressColor dressColor = DressManager.getInstance().getDressColor(color);
        if(dressColor == null) {
            return "";
        }
        
        return "style='background-color:" + dressColor.color + "'";
    }
}
