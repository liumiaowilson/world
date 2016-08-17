package org.wilson.world.habit;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.HabitManager;
import org.wilson.world.model.Habit;
import org.wilson.world.model.Idea;

public class HabitIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Habit";
    }

    @Override
    public Object convert(Idea idea) {
        Habit habit = new Habit();
        habit.name = idea.name;
        habit.description = idea.content;
        habit.interval = 1;
        return habit;
    }

    @Override
    public void save(Object converted) {
        HabitManager.getInstance().createHabit((Habit) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToHabit;
    }

}
