package org.wilson.world.romance;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.RomanceFactorManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.RomanceFactor;

public class RomanceFactorIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Romance Factor";
    }

    @Override
    public Object convert(Idea idea) {
        RomanceFactor factor = new RomanceFactor();
        factor.name = idea.name;
        factor.content = idea.content;
        return factor;
    }

    @Override
    public void save(Object converted) {
        RomanceFactorManager.getInstance().createRomanceFactor((RomanceFactor) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToRomanceFactor;
    }

}
