package org.wilson.world.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

public class TimeUtils {
    public static final long HOUR_DURATION = 60 * 60 * 1000L;
    
    public static final long DAY_DURATION = HOUR_DURATION * 24;
    
    public static String getTimeReadableString(long period) {
        if(period > DAY_DURATION) {
            int result = (int)(period / DAY_DURATION);
            return result + " Days";
        }
        else if(period > HOUR_DURATION) {
            int result = (int)(period / HOUR_DURATION);
            return result + " Hours";
        }
        else {
            return "Less than 1 hour";
        }
    }
    
    public static String getRemainingTime(long time) {
        long now = System.currentTimeMillis();
        long remain = time - now;
        if(remain < 0) {
            return "Expired";
        }
        else {
            return getTimeReadableString(remain);
        }
    }
    
    private static Date parse(String str, String pattern, TimeZone tz) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(tz);
        try {
            return format.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static Date parseTaskAttrDefDate(String str, TimeZone tz) {
        if(StringUtils.isBlank(str) || tz == null) {
            return null;
        }
        
        return parse(str, "yyyy-MM-dd", tz);
    }
    
    public static Date parseTaskAttrDefDateTime(String str, TimeZone tz) {
        if(StringUtils.isBlank(str) || tz == null) {
            return null;
        }
        
        return parse(str, "yyyy-MM-dd HH:mm", tz);
    }
}
