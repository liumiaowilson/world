package org.wilson.world.health;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.HealthManager;
import org.wilson.world.today.TodayContentProvider;

public class BioCurveTodayContentProvider implements TodayContentProvider {
    private static final Logger logger = Logger.getLogger(BioCurveTodayContentProvider.class);
    
    @Override
    public String getName() {
        return "Bio Curve Of The Day";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        try {
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            String healthOfTheDay = HealthManager.getInstance().getSuggestionOfToday(tz);
            if(StringUtils.isBlank(healthOfTheDay)) {
                return null;
            }
            else {
                return healthOfTheDay;
            }
        }
        catch(Exception e) {
            logger.error(e);
            return null;
        }
    }

}
