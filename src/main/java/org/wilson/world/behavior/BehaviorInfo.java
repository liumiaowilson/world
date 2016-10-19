package org.wilson.world.behavior;

import java.util.Calendar;
import java.util.TimeZone;

public class BehaviorInfo {
    public long time;
    
    public String timeStr;
    
    public int count;
    
    public void init(TimeZone tz) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        this.timeStr = "Date.UTC(" + year + "," + month + "," + day + ")";
    }
}
