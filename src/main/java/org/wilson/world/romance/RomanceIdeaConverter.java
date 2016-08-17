package org.wilson.world.romance;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.RomanceManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Romance;

public class RomanceIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Romance";
    }

    @Override
    public Object convert(Idea idea) {
        Romance romance = new Romance();
        romance.name = idea.name;
        romance.content = idea.content;
        return romance;
    }

    @Override
    public void save(Object converted) {
        RomanceManager.getInstance().createRomance((Romance) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToRomance;
    }

}
