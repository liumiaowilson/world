package org.wilson.world.character;

import org.wilson.world.ext.Scriptable;

public interface DeathExecution {
    public static final String EXTENSION_POINT = "death.exec";
    
    @Scriptable(name = EXTENSION_POINT, params = { "damage" })
    public void receiveDeathPenalty(int damage);
}
