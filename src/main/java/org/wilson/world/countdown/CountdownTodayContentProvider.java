package org.wilson.world.countdown;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.CountdownManager;
import org.wilson.world.manager.URLManager;
import org.wilson.world.today.TodayContentProvider;

public class CountdownTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Countdown Of The Day";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
        List<ICountdown> countdowns = CountdownManager.getInstance().getCountdowns(tz);
        if(countdowns == null || countdowns.isEmpty()) {
            return null;
        }
        else {
            Collections.sort(countdowns, new Comparator<ICountdown>(){

                @Override
                public int compare(ICountdown o1, ICountdown o2) {
                    return (int) (o1.getTarget().getTime() - o2.getTarget().getTime());
                }
                
            });
            
            StringBuilder sb = new StringBuilder();
            sb.append("Here are the events [");
            for(int i = 0; i < countdowns.size(); i++) {
                sb.append(countdowns.get(i).getName());
                if(i != countdowns.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]. <a href=\"");
            sb.append(URLManager.getInstance().getBaseUrl());
            sb.append("/jsp/countdown.jsp\">Go</a>");
            return sb.toString();
        }
    }

}
