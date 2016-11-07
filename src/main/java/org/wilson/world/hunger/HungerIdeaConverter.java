package org.wilson.world.hunger;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.HungerManager;
import org.wilson.world.model.Hunger;
import org.wilson.world.model.Idea;

public class HungerIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Hunger";
    }

    @Override
    public Object convert(Idea idea) {
        Hunger hunger = new Hunger();
        hunger.name = idea.name;
        hunger.content = idea.content;
        return hunger;
    }

    @Override
    public void save(Object converted) {
        HungerManager.getInstance().createHunger((Hunger) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToHunger;
    }

}
