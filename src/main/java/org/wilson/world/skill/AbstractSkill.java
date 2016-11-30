package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "generic user skill", name = "system.skill")
public abstract class AbstractSkill extends CommonSkill {
    public abstract boolean canTrigger(Map<String, Object> args);

    public abstract void trigger(Map<String, Object> args);
}
