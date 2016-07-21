package org.wilson.world.task;

import java.util.Date;
import java.util.TimeZone;

public interface TaskSeedPattern {
    /**
     * Protocols are like "calendar:"
     * @return
     */
    public String getProtocol();
    
    public boolean canStart(TimeZone tz, Date date, String templateValue);
    
    public String getDescription();
}
