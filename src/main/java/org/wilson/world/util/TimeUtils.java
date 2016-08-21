package org.wilson.world.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

public class TimeUtils {
    public static final long SECOND_DURATION = 1000L;
    
    public static final long MINUTE_DURATION = 60 * SECOND_DURATION;
    
    public static final long HOUR_DURATION = 60 * MINUTE_DURATION;
    
    public static final long DAY_DURATION = HOUR_DURATION * 24;
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    
    public static String getTimeReadableString(long period) {
        if(period > DAY_DURATION) {
            int result = (int)(period / DAY_DURATION);
            return result + " Days";
        }
        else if(period > HOUR_DURATION) {
            int result = (int)(period / HOUR_DURATION);
            return result + " Hours";
        }
        else if(period > MINUTE_DURATION) {
            int result = (int)(period / MINUTE_DURATION);
            return result + " Minutes";
        }
        else if(period > SECOND_DURATION) {
            int result = (int)(period / SECOND_DURATION);
            return result + " Seconds";
        }
        else {
            return "Less than 1 second";
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
    
    public static Date parseDate(String str, TimeZone tz) {
        if(StringUtils.isBlank(str) || tz == null) {
            return null;
        }
        
        return parse(str, DATE_FORMAT, tz);
    }
    
    public static Date parseDateTime(String str, TimeZone tz) {
        if(StringUtils.isBlank(str) || tz == null) {
            return null;
        }
        
        return parse(str, DATETIME_FORMAT, tz);
    }
    
    public static String toDateString(long time, TimeZone tz) {
        return toDateString(new Date(time), tz);
    }
    
    public static String toDateTimeString(long time, TimeZone tz) {
        return toDateTimeString(new Date(time), tz);
    }
    
    public static String toDateString(Date time, TimeZone tz) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if(tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }
    
    public static String toDateTimeString(Date time, TimeZone tz) {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
        if(tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }
    
    public static Date fromDateString(String str, TimeZone tz) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if(tz != null) {
            format.setTimeZone(tz);
        }
        return format.parse(str);
    }
    
    public static Date fromDateTimeString(String str, TimeZone tz) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
        if(tz != null) {
            format.setTimeZone(tz);
        }
        return format.parse(str);
    }
    
    public static String getDateUTCString(long time, TimeZone tz) {
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
        String timeStr = "Date.UTC(" + year + "," + month + "," + day + ")";
        return timeStr;
    }
    
    public static String getDateTimeUTCString(long time, TimeZone tz) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String timeStr = "Date.UTC(" + year + "," + month + "," + day + "," + hour + "," + minute + ")";
        return timeStr;
    }
}
