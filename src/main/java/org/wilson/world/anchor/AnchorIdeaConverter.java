package org.wilson.world.anchor;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.AnchorManager;
import org.wilson.world.model.Anchor;
import org.wilson.world.model.Idea;

public class AnchorIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Anchor";
    }

    @Override
    public Object convert(Idea idea) {
        Anchor anchor = new Anchor();
        anchor.name = idea.name;
        anchor.stimuli = idea.content;
        anchor.response = idea.content;
        return anchor;
    }

    @Override
    public void save(Object converted) {
        AnchorManager.getInstance().createAnchor((Anchor) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToAnchor;
    }

}
