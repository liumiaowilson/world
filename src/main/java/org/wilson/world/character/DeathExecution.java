package org.wilson.world.character;

import org.wilson.world.ext.Scriptable;

public interface DeathExecution {
    public static final String EXTENSION_POINT = "death.exec";
    
    @Scriptable(name = EXTENSION_POINT, description = "Execute actions when the character has lost one life.", params = { "damage" })
    public void receiveDeathPenalty(int damage);
}
