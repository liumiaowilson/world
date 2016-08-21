package org.wilson.world.quest;

import org.wilson.world.behavior.SystemBehaviorDef;
import org.wilson.world.behavior.SystemBehaviorDefProvider;
import org.wilson.world.event.EventType;

public class QuestSystemBehaviorDefProvider implements SystemBehaviorDefProvider {
    private SystemBehaviorDef def = null;
    
    public QuestSystemBehaviorDefProvider() {
        def = new SystemBehaviorDef();
        def.setId(-2);
        def.setName("Achieve quest");
        def.setDescription("Behavior of achieving quests");
        def.setKarma(1);
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CreateQuest;
    }

    @Override
    public SystemBehaviorDef getSystemBehaviorDef() {
        return def;
    }

}
