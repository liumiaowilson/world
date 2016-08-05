package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface Festival {
    public void setId(int id);
    
    public int getId();
    
    public String getName();
    
    public String getDescription();
    
    public List<Date> getDates(int yearFrom, int yearTo, TimeZone tz);
}
