package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.ext.Scriptable;

public interface SkillTrigger {
    public static final String EXTENSION_POINT = "skill.trigger";
    
    @Scriptable(name = EXTENSION_POINT, description = "Trigger the skill. Assign the value of this action to skill data.", params = { "args" })
    public void trigger(Map<String, Object> args);
}
