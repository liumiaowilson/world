package org.wilson.world.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class CalendarTaskSeedPattern implements TaskSeedPattern {
    private static final Logger logger = Logger.getLogger(CalendarTaskSeedPattern.class);
    
    public static final String PROTOCOL_NAME = "calendar:";
    public static final String FORMAT = "yyyy-MM-dd";
    
    @Override
    public String getProtocol() {
        return PROTOCOL_NAME;
    }

    @Override
    public boolean canStart(TimeZone tz, Date date, String templateValue) {
        if(templateValue == null) {
            return false;
        }
        if(date == null) {
            date = new Date();
        }
        if(templateValue.startsWith(PROTOCOL_NAME)) {
            templateValue = templateValue.substring(PROTOCOL_NAME.length());
        }
        
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        format.setTimeZone(tz);
        try {
            Date d = format.parse(templateValue);
            return d.getTime() < date.getTime();
        } catch (ParseException e) {
            logger.error(e);
        }
        
        return false;
    }

    @Override
    public String getDescription() {
        return "Use this in the format: " + PROTOCOL_NAME + FORMAT;
    }

}
