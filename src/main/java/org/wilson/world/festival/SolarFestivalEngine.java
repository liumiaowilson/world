package org.wilson.world.festival;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SolarFestivalEngine implements FestivalEngine {
    private static final Logger logger = Logger.getLogger(SolarFestivalEngine.class);
    
    public static final String NAME = "solar";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Date> getDates(String pattern, int yearFrom, int yearTo, TimeZone tz) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        //pattern is like MM/dd
        if(StringUtils.isBlank(pattern)) {
            return Collections.emptyList();
        }
        String [] items = pattern.split("/");
        if(items.length != 2) {
            return Collections.emptyList();
        }
        
        try {
            int month = Integer.parseInt(items[0]);
            int day = Integer.parseInt(items[1]);
            
            List<Date> ret = new ArrayList<Date>();
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setTimeZone(tz);
            for(int year = yearFrom; year <= yearTo; year++) {
                String str = year + "-" + month + "-" + day;
                Date date = format.parse(str);
                ret.add(date);
            }
            
            return ret;
        }
        catch(Exception e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }

}
