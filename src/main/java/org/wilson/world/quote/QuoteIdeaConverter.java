package org.wilson.world.quote;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.QuoteManager;
import org.wilson.world.model.Idea;
import org.wilson.world.model.Quote;

public class QuoteIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Quote";
    }

    @Override
    public Object convert(Idea idea) {
        Quote quote = new Quote();
        quote.name = idea.name;
        quote.content = idea.content;
        return quote;
    }

    @Override
    public void save(Object converted) {
        QuoteManager.getInstance().createQuote((Quote) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToQuote;
    }

}
