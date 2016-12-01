package org.wilson.world.festival;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic festivals", name = "system.festival")
public abstract class AbstractFestival extends SystemFestival {

    public abstract List<Date> getDates(int yearFrom, int yearTo, TimeZone tz);
}
