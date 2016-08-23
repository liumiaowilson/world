package org.wilson.world.festival;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.wilson.world.countdown.Countdown;
import org.wilson.world.countdown.ICountdown;
import org.wilson.world.countdown.ICountdownProvider;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.FestivalDataManager;
import org.wilson.world.model.CalendarEvent;
import org.wilson.world.util.TimeUtils;

public class FestivalCountdownProvider implements ICountdownProvider {
    private static final Logger logger = Logger.getLogger(FestivalCountdownProvider.class);

    @Override
    public List<ICountdown> getCountdowns(TimeZone tz) {
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        List<ICountdown> ret = new ArrayList<ICountdown>();
        
        List<CalendarEvent> events = FestivalDataManager.getInstance().getCalendarEvents(tz);
        long ahead = TimeUtils.DAY_DURATION * ConfigManager.getInstance().getConfigAsInt("countdown.ahead.duration.days", 30);
        long now = System.currentTimeMillis();
        for(CalendarEvent event : events) {
            try {
                Date target = TimeUtils.fromDateString(event.start, tz);
                if(now + ahead > target.getTime() && now < target.getTime()) {
                    Countdown cd = new Countdown(event.title, target);
                    ret.add(cd);
                }
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
        
        return ret;
    }

}
