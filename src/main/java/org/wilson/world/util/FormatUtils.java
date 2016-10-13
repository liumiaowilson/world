package org.wilson.world.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;

public class FormatUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String format(Date date) {
        return format(date, null);
    }
    

    public static double getRoundedValue(double d) {
        return (double) Math.round(d * 10) / 10;
    }
    
    public static String format(Date date, TimeZone tz) {
        if(date == null) {
            return null;
        }
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(tz);
        return format.format(date);
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
