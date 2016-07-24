package org.wilson.world.status;

public class SkilledStatus extends SystemStatus {
    public static final String NAME = "skilled";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "When this effect is on, the speed of gaining base experience will be doubled.";
    }

}
