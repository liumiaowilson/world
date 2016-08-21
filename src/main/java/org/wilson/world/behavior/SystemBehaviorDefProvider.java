package org.wilson.world.behavior;

import org.wilson.world.event.EventType;

public interface SystemBehaviorDefProvider {
    public EventType getEventType();
    
    public SystemBehaviorDef getSystemBehaviorDef();
}
