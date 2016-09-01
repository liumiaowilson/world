package org.wilson.world.mission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.MissionManager;
import org.wilson.world.manager.URLManager;
import org.wilson.world.today.TodayContentProvider;

public class MissionTodayContentProvider implements TodayContentProvider {

    @Override
    public String getName() {
        return "Mission Of The Day";
    }

    @Override
    public String getContent(HttpServletRequest request) {
        List<Mission> missions = MissionManager.getInstance().getRecommendedMissions();
        if(missions.isEmpty()) {
            return null;
        }
        else {
            return "Easy missions found. <a href=\"" + URLManager.getInstance().getBaseUrl() + "/jsp/mission.jsp\">Go</a>";
        }
    }

}
