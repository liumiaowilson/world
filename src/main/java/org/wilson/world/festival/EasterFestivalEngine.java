package org.wilson.world.festival;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EasterFestivalEngine implements FestivalEngine {
    private static final Logger logger = Logger.getLogger(EasterFestivalEngine.class);
    
    public static final String NAME = "easter";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Date> getDates(String pattern, int yearFrom, int yearTo, TimeZone tz) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        //pattern is anything but null
        if(StringUtils.isBlank(pattern)) {
            return Collections.emptyList();
        }
        
        try {
            List<Date> ret = new ArrayList<Date>();
            
            for(int year = yearFrom; year <= yearTo; year++) {
                //algorithm from internet
                int a = year % 19; 
                int b = year / 100;
                int c = year % 100;
                int d = b / 4;
                int e = b % 4;
                int g = (8 * b + 13) / 25;
                int h = (19 * a + b - d - g + 15) % 30;
                int j = c / 4;
                int k = c % 4;
                int m = (a + 11 * h) / 319;
                int r = (2 * e + 2 * j - k - h - m + 32) % 7;
                int n = (h - m + r + 90) / 25;
                int p = (h - m + r + n + 19) % 32;
                
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(tz);
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, n - 1);
                cal.set(Calendar.DATE, p);
                ret.add(cal.getTime());
            }
            
            return ret;
        }
        catch(Exception e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }

}
