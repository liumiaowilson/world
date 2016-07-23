package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Contact;
import org.wilson.world.model.ContactAttr;

public class ContactManager implements ItemTypeProvider {
    public static final String NAME = "contact";
    
    private static ContactManager instance;
    
    private DAO<Contact> dao = null;
    
    @SuppressWarnings("unchecked")
    private ContactManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Contact.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ContactManager getInstance() {
        if(instance == null) {
            instance = new ContactManager();
        }
        return instance;
    }
    
    public void createContact(Contact contact) {
        if(this.hasContact(contact)) {
            throw new DataException("Contact with same name already exists.");
        }
        
        for(ContactAttr attr : contact.attrs) {
            ContactAttrManager.getInstance().processContactAttr(attr);
        }
        
        this.dao.create(contact);
        
        for(ContactAttr attr : contact.attrs) {
            attr.contactId = contact.id;
            ContactAttrManager.getInstance().createContactAttr(attr);
        }
    }
    
    public Contact getContact(int id) {
        Contact contact = this.dao.get(id);
        if(contact != null) {
            contact.attrs = ContactAttrManager.getInstance().getContactAttrsByContactId(contact.id);
            return contact;
        }
        else {
            return null;
        }
    }
    
    public boolean hasContact(Contact contact) {
        if(contact == null) {
            return false;
        }
        for(Contact t : this.dao.getAll()) {
            if(t.name.equals(contact.name) && t.id != contact.id) {
                return true;
            }
        }
        return false;
    }
    
    public Contact getContact(String name) {
        for(Contact contact : this.dao.getAll()) {
            if(contact.name.equals(name)) {
                contact.attrs = ContactAttrManager.getInstance().getContactAttrsByContactId(contact.id);
                return contact;
            }
        }
        return null;
    }
    
    public List<Contact> getContacts() {
        List<Contact> result = new ArrayList<Contact>();
        for(Contact contact : this.dao.getAll()) {
            contact.attrs = ContactAttrManager.getInstance().getContactAttrsByContactId(contact.id);
            result.add(contact);
        }
        return result;
    }
    
    private boolean hasContactAttr(List<ContactAttr> attrs, ContactAttr attr) {
        for(ContactAttr p : attrs) {
            if(p.id == attr.id) {
                return true;
            }
        }
        return false;
    }
    
    public void updateContact(Contact contact) {
        if(this.hasContact(contact)) {
            throw new DataException("Contact with same name already exists.");
        }
        
        for(ContactAttr attr : contact.attrs) {
            ContactAttrManager.getInstance().processContactAttr(attr);
        }
        
        this.dao.update(contact);
        
        List<ContactAttr> oldAttrs = ContactAttrManager.getInstance().getContactAttrsByContactId(contact.id);
        List<ContactAttr> create = new ArrayList<ContactAttr>();
        List<ContactAttr> update = new ArrayList<ContactAttr>();
        List<ContactAttr> delete = new ArrayList<ContactAttr>();
        for(ContactAttr p : contact.attrs) {
            if(p.id == 0) {
                create.add(p);
            }
            else if(hasContactAttr(oldAttrs, p)) {
                update.add(p);
            }
            else {
                //will not get here
            }
        }
        for(ContactAttr p : oldAttrs) {
            if(!hasContactAttr(contact.attrs, p)) {
                delete.add(p);
            }
        }
        
        for(ContactAttr attr : create) {
            attr.contactId = contact.id;
            ContactAttrManager.getInstance().createContactAttr(attr);
        }
        
        for(ContactAttr attr : update) {
            attr.contactId = contact.id;
            ContactAttrManager.getInstance().updateContactAttr(attr);
        }
        
        for(ContactAttr attr : delete) {
            ContactAttrManager.getInstance().deleteContactAttr(attr.id);
        }
    }
    
    public void deleteContact(int id) {
        Contact oldcontact = this.getContact(id);
        
        for(ContactAttr attr : oldcontact.attrs) {
            ContactAttrManager.getInstance().deleteContactAttr(attr.id);
        }
        
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Contact;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Contact contact = (Contact)target;
        return String.valueOf(contact.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public ContactAttr getContactAttr(Contact contact, String attrName) {
        if(contact == null || StringUtils.isBlank(attrName)) {
            return null;
        }
        if(!ContactAttrDefManager.getInstance().isValidContactAttrName(attrName)) {
            return null;
        }
        for(ContactAttr attr : contact.attrs) {
            if(attr.name.equals(attrName)) {
                return attr;
            }
        }
        return null;
    }
    
    public Contact findContact(String attrName, String attrValue) {
        for(Contact contact : this.getContacts()) {
            ContactAttr attr = this.getContactAttr(contact, attrName);
            if(attr != null && attr.value != null && attr.value.equals(attrValue)) {
                return contact;
            }
        }
        return null;
    }
}
