package org.wilson.world.festival;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MonthFestivalEngine implements FestivalEngine {
    private static final Logger logger = Logger.getLogger(MonthFestivalEngine.class);
    
    public static final String NAME = "month";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Date> getDates(String pattern, int yearFrom, int yearTo, TimeZone tz) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        //pattern is like week/day
        if(StringUtils.isBlank(pattern)) {
            return Collections.emptyList();
        }
        String [] items = pattern.split("/");
        if(items.length != 2) {
            return Collections.emptyList();
        }
        
        try {
            int week = Integer.parseInt(items[0]);
            int day = Integer.parseInt(items[1]);
            
            //monday as the first day
            day += 1;
            if(day == 8) {
                day = 1;
            }
            
            List<Date> ret = new ArrayList<Date>();
            
            for(int year = yearFrom; year <= yearTo; year++) {
                Calendar cal = Calendar.getInstance();
                cal.setLenient(true);
                cal.setTimeZone(tz);
                cal.set(Calendar.YEAR, year);
                for(int month = 0; month < 12; month++) {
                	cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, week);
                    cal.set(Calendar.DAY_OF_WEEK, day);
                    ret.add(cal.getTime());
                }
            }
            
            return ret;
        }
        catch(Exception e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }

}
