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
    
    public int dice(int p1, int p2) {
        int v1 = r.nextInt(p1);
        int v2 = r.nextInt(p2);
        return v1 - v2;
    }
    
    public boolean dice(int min_p, int max_p, int min_value, int max_value, int value) {
        double ratio = Math.abs(min_value - value) * 1.0 / Math.abs(min_value - max_value);
        int p = (int) (Math.abs(max_p - min_p) * ratio + min_p);
        return dice(p);
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
    
    public int random(int max) {
        return r.nextInt(max);
    }
    
    public int roll(int base, double min, double max) {
        int min_value = (int) (base * min);
        int max_value = (int) (base * max);
        return this.roll(min_value, max_value);
    }
    
    public int roll(int min, int max) {
        return min + r.nextInt(max - min);
    }
    
    public int roll(int min, int max, int pivot) {
        double g = this.r.nextGaussian();
        int ret = (int) (pivot + g);
        if(ret < min) {
            ret = min;
        }
        else if(ret > max) {
            ret = max;
        }
        return ret;
    }
    
    public int roll(int min, int max, double pivot_ratio) {
        int pivot = (int) (min + (max - min) * pivot_ratio);
        return this.roll(min, max, pivot);
    }
    
    public int roll(int min, int max, int min_input, int max_input, int input) {
        double pivot = Math.abs(min_input - input) * 1.0 / Math.abs(min_input - max_input);
        return roll(min, max, pivot);
    }
}
