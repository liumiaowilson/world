package org.wilson.world.skill;

import java.util.List;

import org.wilson.world.manager.UserSkillManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.UserSkill;
import org.wilson.world.monitor.MonitorParticipant;

public class UpgradeSkillMonitor implements MonitorParticipant {
	private Alert alert;
	
	public UpgradeSkillMonitor() {
		this.alert = new Alert();
		this.alert.id = "Upgrade skills";
		this.alert.message = "Please upgrade the skills as soon as possible.";
		this.alert.canAck = true;
		this.alert.url = "trainer_skill_upgrade.jsp";
	}
	
	@Override
	public boolean isOK() {
		List<UserSkill> skills = UserSkillManager.getInstance().getUpgradableUserSkills();
		return skills.isEmpty();
	}

	@Override
	public Alert getAlert() {
		return this.alert;
	}

}
