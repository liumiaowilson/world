package org.wilson.world.skill;

import java.util.Map;

import org.wilson.world.ext.Scriptable;

public interface SkillCanTrigger {
    public static final String EXTENSION_POINT = "skill.can_trigger";
    
    @Scriptable(name = EXTENSION_POINT, description = "Check if the skill can trigger. Assign the value of this action to skill data.", params = { "args" })
    public boolean canTrigger(Map<String, Object> args);
}
