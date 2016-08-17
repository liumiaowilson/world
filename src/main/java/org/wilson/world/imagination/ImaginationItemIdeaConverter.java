package org.wilson.world.imagination;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ImaginationItemManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.ImaginationItem;

public class ImaginationItemIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Imagination Item";
    }

    @Override
    public Object convert(Idea idea) {
        ImaginationItem item = new ImaginationItem();
        item.name = idea.name;
        item.content = idea.content;
        return item;
    }

    @Override
    public void save(Object converted) {
        ImaginationItemManager.getInstance().createImaginationItem((ImaginationItem) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToImaginationItem;
    }

}
