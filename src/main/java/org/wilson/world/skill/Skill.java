package org.wilson.world.skill;

import java.util.Map;

public interface Skill {
    public void setId(int id);
    
    public int getId();
    
    public String getName();
    
    public String getDescription();
    
    public String getType();
    
    public String getScope();
    
    public String getTarget();
    
    public int getCost();
    
    public int getCooldown();
    
    public boolean canTrigger(Map<String, Object> args);
    
    public void trigger(Map<String, Object> args);
}
