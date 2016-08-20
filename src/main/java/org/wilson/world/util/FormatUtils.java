package org.wilson.world.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;

public class FormatUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String format(Date date) {
        if(date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }
    

    public static double getRoundedValue(double d) {
        return (double) Math.round(d * 10) / 10;
    }
    
    public static String format(Date date, TimeZone tz) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(tz);
        cal.setTime(date);
        Date d = cal.getTime();
        
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(d);
    }
    
    public static String safeString(String str) {
        if(str == null) {
            return str;
        }
        int pos = str.indexOf("\n");
        if(pos >= 0) {
            return str.substring(0, pos);
        }
        else {
            return str;
        }
    }
    
    public static String htmlToText(String html) {
        return Jsoup.parse(html).text();
    }
}
