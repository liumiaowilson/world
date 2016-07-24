package org.wilson.world.manager;

import java.util.Random;

public class DiceManager {
    private static DiceManager instance;
    
    private static final String RANGE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
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
    
    public String randomName(int maxLength) {
        int length = 1 + r.nextInt(maxLength);
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < length; i++) {
            sb.append(this.randomChar());
        }
        return sb.toString();
    }
    
    private char randomChar() {
        int pos = r.nextInt(RANGE.length());
        return RANGE.charAt(pos);
    }
}
