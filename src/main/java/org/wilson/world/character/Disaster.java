package org.wilson.world.character;

import org.wilson.world.ext.Scriptable;

public interface Disaster {
    public static final String EXTENSION_POINT = "world.disaster";
    
    @Scriptable(name = EXTENSION_POINT, description = "Get the damage caused by the world disaster.", params = { "level", "max_hp", "hp" })
    public int getDamage(int level, int max_hp, int hp);
}
