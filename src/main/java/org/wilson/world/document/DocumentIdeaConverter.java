package org.wilson.world.document;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.DocumentManager;
import org.wilson.world.model.Document;
import org.wilson.world.model.Idea;

public class DocumentIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Document";
    }

    @Override
    public Object convert(Idea idea) {
        Document doc = new Document();
        doc.name = idea.name;
        doc.content = idea.content;
        return doc;
    }

    @Override
    public void save(Object converted) {
        DocumentManager.getInstance().createDocument((Document) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToDocument;
    }

}
