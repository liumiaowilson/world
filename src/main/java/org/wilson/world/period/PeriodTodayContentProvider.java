package org.wilson.world.period;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.PeriodManager;
import org.wilson.world.today.TodayContentProvider;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;

public class PeriodTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Period Track";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        PeriodRecord next = PeriodManager.getInstance().getNextExpectedPeriodRecord();
        
        boolean onPeriod = PeriodManager.getInstance().isOnPeriod();
        long now = System.currentTimeMillis();
        if(onPeriod) {
            if(next != null) {
                double days = FormatUtils.getRoundedValue((next.end - now) * 1.0 / TimeUtils.DAY_DURATION);
                return "On period, which will end in " + days + " days.";
            }
            else {
                return "On period.";
            }
        }
        else {
            if(next != null) {
                double days = FormatUtils.getRoundedValue((next.start - now) * 1.0 / TimeUtils.DAY_DURATION);
                return "Off period, and the period will start in " + days + " days.";
            }
            else {
                return "Off period.";
            }
        }
    }

}
