package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface FestivalEngine {
    public String getName();
    
    public List<Date> getDates(String pattern, int yearFrom, int yearTo, TimeZone tz);
}
