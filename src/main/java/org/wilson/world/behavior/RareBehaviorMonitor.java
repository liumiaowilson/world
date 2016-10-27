package org.wilson.world.behavior;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.manager.BehaviorManager;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.util.TimeUtils;

public class RareBehaviorMonitor implements MonitorParticipant {
	private Alert alert;
	
	public RareBehaviorMonitor() {
		this.alert = new Alert();
		this.alert.id = "Rare behaviors found";
		this.alert.message = "Please execute these rare behaviors.";
		this.alert.canAck = true;
		this.alert.url = "javascript:jumpTo('behavior_track.jsp')";
	}
	
	@Override
	public boolean isOK() {
		List<BehaviorFrequency> freqs = BehaviorManager.getInstance().getBehaviorFrequencies();
		if(!freqs.isEmpty()) {
			List<String> names = new ArrayList<String>();
			int rareDays = ConfigManager.getInstance().getConfigAsInt("rare_behavior.period.days", 7);
			long rarePeriod = rareDays * TimeUtils.DAY_DURATION;
			for(BehaviorFrequency freq : freqs) {
				if(freq.lastInMillis > rarePeriod) {
					names.add(freq.name);
				}
			}
			
			if(!names.isEmpty()) {
				StringBuilder sb = new StringBuilder("Please execute these behaviors [");
				for(int i = 0; i < names.size(); i++) {
					sb.append(names.get(i));
					if(i != names.size() - 1) {
						sb.append(",");
					}
				}
				sb.append("]");
				this.alert.message = sb.toString();
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Alert getAlert() {
		return this.alert;
	}

}
