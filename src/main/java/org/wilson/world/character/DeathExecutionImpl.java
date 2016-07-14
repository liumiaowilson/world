package org.wilson.world.character;

import org.wilson.world.manager.ExpManager;

public class DeathExecutionImpl implements DeathExecution {

    @Override
    public void receiveDeathPenalty(int damage) {
        int level = ExpManager.getInstance().getLevel();
        int exp = ExpManager.getInstance().getLevelExp(level);
        ExpManager.getInstance().setExp(exp);
    }

}
