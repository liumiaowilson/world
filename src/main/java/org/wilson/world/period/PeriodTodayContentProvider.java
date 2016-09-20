package org.wilson.world.period;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.PeriodManager;
import org.wilson.world.today.TodayContentProvider;

public class PeriodTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Period Track";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        boolean onPeriod = PeriodManager.getInstance().isOnPeriod();
        if(onPeriod) {
            return "Today she is on period.";
        }
        else {
            return null;
        }
    }

}
