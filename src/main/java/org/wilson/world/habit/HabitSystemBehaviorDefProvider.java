package org.wilson.world.habit;

import org.wilson.world.behavior.SystemBehaviorDef;
import org.wilson.world.behavior.SystemBehaviorDefProvider;
import org.wilson.world.event.EventType;

public class HabitSystemBehaviorDefProvider implements SystemBehaviorDefProvider {
    private SystemBehaviorDef def = null;
    
    public HabitSystemBehaviorDefProvider() {
        def = new SystemBehaviorDef();
        def.setId(-1);
        def.setName("Check habit");
        def.setDescription("Behavior of checking habits");
        def.setKarma(1);
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CheckHabit;
    }

    @Override
    public SystemBehaviorDef getSystemBehaviorDef() {
        return def;
    }

}
