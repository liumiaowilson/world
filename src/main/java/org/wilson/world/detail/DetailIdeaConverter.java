package org.wilson.world.detail;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.DetailManager;
import org.wilson.world.model.Detail;
import org.wilson.world.model.Idea;

public class DetailIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Detail";
    }

    @Override
    public Object convert(Idea idea) {
        Detail detail = new Detail();
        detail.name = idea.name;
        detail.content = idea.content;
        return detail;
    }

    @Override
    public void save(Object converted) {
        DetailManager.getInstance().createDetail((Detail)converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToDetail;
    }

}
