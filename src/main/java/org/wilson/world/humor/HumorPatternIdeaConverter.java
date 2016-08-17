package org.wilson.world.humor;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.HumorPatternManager;
import org.wilson.world.model.HumorPattern;
import org.wilson.world.model.Idea;

public class HumorPatternIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Humor Pattern";
    }

    @Override
    public Object convert(Idea idea) {
        HumorPattern pattern = new HumorPattern();
        pattern.name = idea.name;
        pattern.content = idea.content;
        return pattern;
    }

    @Override
    public void save(Object converted) {
        HumorPatternManager.getInstance().createHumorPattern((HumorPattern) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToHumorPattern;
    }

}
