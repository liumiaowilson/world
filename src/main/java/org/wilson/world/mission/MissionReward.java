package org.wilson.world.mission;

public interface MissionReward {
    public String getName();
    
    public int getWorth();
    
    public void deliver();
}
