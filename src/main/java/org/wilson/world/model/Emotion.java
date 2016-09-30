package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.emotion.EmotionType;

public class Emotion {
    public int id;
    
    public String name;
    
    public String description;
    
    public int ecstacy;
    
    public int grief;
    
    public int admiration;
    
    public int loathing;
    
    public int rage;
    
    public int terror;
    
    public int vigilance;
    
    public int amazement;
    
    public static String getName(EmotionType type) {
        if(EmotionType.Ecstacy == type) {
            return "ecstacy";
        }
        else if(EmotionType.Grief == type) {
            return "grief";
        }
        else if(EmotionType.Admiration == type) {
            return "admiration";
        }
        else if(EmotionType.Loathing == type) {
            return "loathing";
        }
        else if(EmotionType.Rage == type) {
            return "rage";
        }
        else if(EmotionType.Terror == type) {
            return "terror";
        }
        else if(EmotionType.Vigilance == type) {
            return "vigilance";
        }
        else if(EmotionType.Amazement == type) {
            return "amazement";
        }
        else {
            return null;
        }
    }
    
    public int getValue(EmotionType type) {
        if(EmotionType.Ecstacy == type) {
            return ecstacy;
        }
        else if(EmotionType.Grief == type) {
            return grief;
        }
        else if(EmotionType.Admiration == type) {
            return admiration;
        }
        else if(EmotionType.Loathing == type) {
            return loathing;
        }
        else if(EmotionType.Rage == type) {
            return rage;
        }
        else if(EmotionType.Terror == type) {
            return terror;
        }
        else if(EmotionType.Vigilance == type) {
            return vigilance;
        }
        else if(EmotionType.Amazement == type) {
            return amazement;
        }
        else {
            return 0;
        }
    }
    
    public List<EmotionType> getEmotionTypes() {
        List<EmotionType> ret = new ArrayList<EmotionType>();
        
        if(ecstacy != 0) {
            ret.add(EmotionType.Ecstacy);
        }
        
        if(grief != 0) {
            ret.add(EmotionType.Grief);
        }
        
        if(admiration != 0) {
            ret.add(EmotionType.Admiration);
        }
        
        if(loathing != 0) {
            ret.add(EmotionType.Loathing);
        }
        
        if(rage != 0) {
            ret.add(EmotionType.Rage);
        }
        
        if(terror != 0) {
            ret.add(EmotionType.Terror);
        }
        
        if(vigilance != 0) {
            ret.add(EmotionType.Vigilance);
        }
        
        if(amazement != 0) {
            ret.add(EmotionType.Amazement);
        }
        
        return ret;
    }
}
