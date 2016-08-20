package org.wilson.world.manager;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.wilson.world.util.TimeUtils;

public class HealthManager {
    public static final int PERIOD_ENERGY = 23;
    public static final int PERIOD_EMOTION = 28;
    public static final int PERIOD_INTELLIGENCE = 33;
    
    private static HealthManager instance;
    
    private HealthManager() {
        
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
}
