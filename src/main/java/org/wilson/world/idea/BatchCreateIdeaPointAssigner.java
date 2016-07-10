package org.wilson.world.idea;

import java.util.List;

import org.wilson.world.event.Event;
import org.wilson.world.exp.PointAssigner;
import org.wilson.world.model.Idea;

public class BatchCreateIdeaPointAssigner implements PointAssigner{

    @SuppressWarnings("unchecked")
    @Override
    public int getPoint(Event event) {
        List<Idea> ideas = (List<Idea>) event.data.get("data");
        return ideas.size();
    }

}
