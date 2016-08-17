package org.wilson.world.scenario;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ScenarioManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Scenario;

public class ScenarioIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Scenario";
    }

    @Override
    public Object convert(Idea idea) {
        Scenario scenario = new Scenario();
        scenario.name = idea.name;
        scenario.stimuli = idea.content;
        scenario.reaction = idea.content;
        return scenario;
    }

    @Override
    public void save(Object converted) {
        ScenarioManager.getInstance().createScenario((Scenario) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToScenario;
    }

}
