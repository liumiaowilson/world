package org.wilson.world.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String format(Date date) {
        if(date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }
}
