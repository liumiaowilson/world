package org.wilson.world.festival;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.util.Lunar;
import org.wilson.world.util.LunarSolarConverter;
import org.wilson.world.util.Solar;

public class LunarFestivalEngine implements FestivalEngine {
    private static final Logger logger = Logger.getLogger(LunarFestivalEngine.class);
    
    public static final String NAME = "lunar";
    
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
            int lunarMonth = Integer.parseInt(items[0]);
            int lunarDay = Integer.parseInt(items[1]);
            
            List<Date> ret = new ArrayList<Date>();
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setTimeZone(tz);
            for(int year = yearFrom; year <= yearTo; year++) {
                List<Solar> solars = this.getSolar(year, lunarMonth, lunarDay);
                for(Solar solar : solars) {
                    String str = year + "-" + solar.solarMonth + "-" + solar.solarDay;
                    Date date = format.parse(str);
                    ret.add(date);
                }
            }
            
            return ret;
        }
        catch(Exception e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }

    private List<Solar> getSolar(int solarYear, int lunarMonth, int lunarDay) {
        List<Solar> ret = new ArrayList<Solar>();
        for(int i = solarYear - 1; i <= solarYear + 1; i++) {
            Lunar lunar = new Lunar();
            lunar.lunarYear = i;
            lunar.lunarMonth = lunarMonth;
            lunar.lunarDay = lunarDay;
            Solar solar = LunarSolarConverter.LunarToSolar(lunar);
            if(solar.solarYear == solarYear) {
                ret.add(solar);
            }
        }
        return ret;
    }
}
