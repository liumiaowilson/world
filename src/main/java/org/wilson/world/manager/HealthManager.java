package org.wilson.world.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.health.BioCurveTodayContentProvider;
import org.wilson.world.util.TimeUtils;

public class HealthManager {
    public static final String ENERGY_NAME = "Engery";
    public static final String EMOTION_NAME = "Emotion";
    public static final String INTELLIGENCE_NAME = "Intelligence";
    
    public static final int PERIOD_ENERGY = 23;
    public static final int PERIOD_EMOTION = 28;
    public static final int PERIOD_INTELLIGENCE = 33;
    
    private static HealthManager instance;
    
    private HealthManager() {
        TodayManager.getInstance().addTodayContentProvider(new BioCurveTodayContentProvider());
    }
    
    public static HealthManager getInstance() {
        if(instance == null) {
            instance = new HealthManager();
        }
        
        return instance;
    }
    
    public int getBioCurveValueAsPercentage(long lifetime, int period) {
        int days = (int) (lifetime / TimeUtils.DAY_DURATION);
        int span = days % period;
        double degree = 360.0 * span / period;
        double radian = Math.toRadians(degree);
        return (int) (Math.sin(radian) * 100);
    }
    
    private long getLivedTime(TimeZone tz) throws ParseException {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        String birthdayStr = ConfigManager.getInstance().getConfig("user.default.birthday", "1987-04-06");
        Date birthday = TimeUtils.fromDateString(birthdayStr, tz);
        return System.currentTimeMillis() - birthday.getTime();
    }
    
    public int getCurrentEnergyPower(TimeZone tz) throws Exception {
        long lifetime = this.getLivedTime(tz);
        return this.getBioCurveValueAsPercentage(lifetime, PERIOD_ENERGY);
    }
    
    public int getCurrentEmotionPower(TimeZone tz) throws Exception {
        long lifetime = this.getLivedTime(tz);
        return this.getBioCurveValueAsPercentage(lifetime, PERIOD_EMOTION);
    }
    
    public int getCurrentIntelligencePower(TimeZone tz) throws Exception {
        long lifetime = this.getLivedTime(tz);
        return this.getBioCurveValueAsPercentage(lifetime, PERIOD_INTELLIGENCE);
    }
    
    public int [] getPowers(TimeZone tz, int period) throws Exception {
        int [] ret = new int [21];
        
        long lifetime = this.getLivedTime(tz);
        for(int i = -10; i <= 10; i++) {
            ret[10 + i] = this.getBioCurveValueAsPercentage(lifetime + i * TimeUtils.DAY_DURATION, period);
        }
        
        return ret;
    }
    
    public int [] getEnergyPowers(TimeZone tz) throws Exception {
        return this.getPowers(tz, PERIOD_ENERGY);
    }

    public int [] getEmotionPowers(TimeZone tz) throws Exception {
        return this.getPowers(tz, PERIOD_EMOTION);
    }

    public int [] getIntelligencePowers(TimeZone tz) throws Exception {
        return this.getPowers(tz, PERIOD_INTELLIGENCE);
    }
    
    public String getSuggestionOfToday(TimeZone tz) throws Exception {
        List<String> advs = new ArrayList<String>();
        List<String> disadvs = new ArrayList<String>();
        
        int limit = ConfigManager.getInstance().getConfigAsInt("biocurve.show_in_today.limit", 75);
        
        int energy = this.getCurrentEnergyPower(tz);
        if(energy >= limit) {
            advs.add(ENERGY_NAME);
        }
        else if(energy <= -limit) {
            disadvs.add(ENERGY_NAME);
        }
        
        int emotion = this.getCurrentEmotionPower(tz);
        if(emotion >= limit) {
            advs.add(EMOTION_NAME);
        }
        else if(emotion <= -limit) {
            disadvs.add(EMOTION_NAME);
        }
        
        int intelligence = this.getCurrentIntelligencePower(tz);
        if(intelligence >= limit) {
            advs.add(INTELLIGENCE_NAME);
        }
        else if(intelligence <= -limit) {
            disadvs.add(INTELLIGENCE_NAME);
        }
        
        StringBuilder sb = new StringBuilder();
        if(!advs.isEmpty()) {
            sb.append("Your advantages are [<span style=\"color: green\">");
            for(String adv : advs) {
                sb.append(adv + " ");
            }
            sb.append("</span>]. ");
        }
        
        if(!disadvs.isEmpty()) {
            sb.append("Your disadvantages are [<span style=\"color: red\">");
            for(String disadv : disadvs) {
                sb.append(disadv + " ");
            }
            sb.append("</span>]. ");
        }
        
        return sb.toString();
    }
}
