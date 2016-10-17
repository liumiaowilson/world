package org.wilson.world.reaction;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ReactionManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Reaction;

public class ReactionIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Reaction";
    }

    @Override
    public Object convert(Idea idea) {
        Reaction reaction = new Reaction();
        reaction.name = idea.name;
        reaction.condition = idea.content;
        reaction.result = idea.content;
        return reaction;
    }

    @Override
    public void save(Object converted) {
        ReactionManager.getInstance().createReaction((Reaction) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToReaction;
    }

}
