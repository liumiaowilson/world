package org.wilson.world.festival;

import java.util.List;

import org.wilson.world.java.JavaExtensible;
import org.wilson.world.model.CalendarEvent;

@JavaExtensible(description = "Generic calendar event provider", name = "system.calendar")
public interface CalendarEventProvider {
    public String getName();

    public List<CalendarEvent> getCalendarEvents(int yearFrom, int yearTo);
}
