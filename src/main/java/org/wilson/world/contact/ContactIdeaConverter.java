package org.wilson.world.contact;

import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverter;
import org.wilson.world.manager.ContactManager;
import org.wilson.world.model.Contact;
import org.wilson.world.model.Idea;

public class ContactIdeaConverter implements IdeaConverter {

    @Override
    public String getName() {
        return "Contact";
    }

    @Override
    public Object convert(Idea idea) {
        Contact contact = new Contact();
        contact.name = idea.name;
        contact.content = idea.content;
        contact.createdTime = System.currentTimeMillis();
        contact.modifiedTime = contact.createdTime;
        return contact;
    }

    @Override
    public void save(Object converted) {
        ContactManager.getInstance().createContact((Contact) converted);
    }

    @Override
    public EventType getEventType() {
        return EventType.IdeaToContact;
    }

}
