package org.wilson.world.manager;

import java.util.Random;

public class DiceManager {
    private static DiceManager instance;
    
    private Random r = new Random();
    
    private DiceManager() {
        
    }
    
    public static DiceManager getInstance() {
        if(instance == null) {
            instance = new DiceManager();
        }
        return instance;
    }
    
    /**
     * Check the possibility
     * 
     * @param possibility between 0 and 100
     * @return
     */
    public boolean dice(int possibility) {
        int value = r.nextInt(100);
        return value < possibility;
    }
}
