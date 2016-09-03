package org.wilson.world.behavior;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.BehaviorDefManager;
import org.wilson.world.model.BehaviorDef;
import org.wilson.world.model.Idea;

public class BehaviorDefIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Behavior Def";
    }

    @Override
    public Object convert(Idea idea) {
        BehaviorDef def = new BehaviorDef();
        def.name = idea.name;
        def.description = idea.content;
        def.karma = 0;
        return def;
    }

    @Override
    public void save(Object converted) {
        BehaviorDef def = (BehaviorDef)converted;
        BehaviorDefManager.getInstance().createBehaviorDef(def);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToBehaviorDef;
    }

}
