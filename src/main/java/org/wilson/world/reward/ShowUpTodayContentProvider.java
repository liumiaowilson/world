package org.wilson.world.reward;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.manager.RewardManager;
import org.wilson.world.today.TodayContentProvider;

public class ShowUpTodayContentProvider implements TodayContentProvider {

	@Override
	public String getName() {
		return "Show Up";
	}

	@Override
	public String getContent(HttpServletRequest request) {
		Reward reward = RewardManager.getInstance().getShowUpReward();
		if(reward != null) {
			return "<button type=\"button\" class=\"btn btn-success\" onclick=\"javascript:showUp()\">Reward</button>";
		}
		
		return null;
	}

}
