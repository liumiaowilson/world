package org.wilson.world.quest;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.QuestDefManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.QuestDef;

public class QuestDefIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Quest Def";
    }

    @Override
    public Object convert(Idea idea) {
        QuestDef def = new QuestDef();
        def.name = idea.name;
        def.content = idea.content;
        def.pay = 5;
        return def;
    }

    @Override
    public void save(Object converted) {
        QuestDefManager.getInstance().createQuestDef((QuestDef) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToQuestDef;
    }

}
