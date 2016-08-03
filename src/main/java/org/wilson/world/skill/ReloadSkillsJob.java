package org.wilson.world.skill;

import org.wilson.world.manager.TrainerSkillManager;
import org.wilson.world.schedule.DefaultJob;

public class ReloadSkillsJob extends DefaultJob {

    @Override
    public void execute() {
        TrainerSkillManager.getInstance().reloadSkills();
    }

}
