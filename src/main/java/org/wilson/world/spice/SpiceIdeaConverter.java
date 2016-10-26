package org.wilson.world.spice;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.SpiceManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Spice;

public class SpiceIdeaConverter implements IdeaConverter {

	@Override
	public String getName() {
		return "Spice";
	}

	@Override
	public Object convert(Idea idea) {
		Spice spice = new Spice();
		spice.name = idea.name;
		spice.prerequisite = idea.name;
		spice.cost = 10;
		spice.content = idea.content;
		return spice;
	}

	@Override
	public void save(Object converted) {
		SpiceManager.getInstance().createSpice((Spice) converted);
	}

	@Override
	public EventType getEventType() {
		return EventType.IdeaToSpice;
	}

}
