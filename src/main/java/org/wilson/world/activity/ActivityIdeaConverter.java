package org.wilson.world.activity;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ActivityManager;
import org.wilson.world.model.Activity;
import org.wilson.world.model.Idea;

public class ActivityIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Activity";
    }

    @Override
    public Object convert(Idea idea) {
        Activity activity = new Activity();
        activity.name = idea.name;
        activity.content = idea.content;
        return activity;
    }

    @Override
    public void save(Object converted) {
        ActivityManager.getInstance().createActivity((Activity) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToActivity;
    }

}
