package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Contact;
import org.wilson.world.model.ContactAttr;
import org.wilson.world.model.ContactAttrDef;

public class ContactAttrManager implements ItemTypeProvider {
    public static final String NAME = "contact_attr";
    
    private static ContactAttrManager instance;
    
    private DAO<ContactAttr> dao = null;
    
    @SuppressWarnings("unchecked")
    private ContactAttrManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(ContactAttr.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ContactAttrManager getInstance() {
        if(instance == null) {
            instance = new ContactAttrManager();
        }
        return instance;
    }
    
    public void createContactAttr(ContactAttr attr) {
        ItemManager.getInstance().checkDuplicate(attr);
        
        this.dao.create(attr);
    }
    
    public void processContactAttr(ContactAttr attr) {
        if(attr == null) {
            return;
        }
        ContactAttrDef def = ContactAttrDefManager.getInstance().getContactAttrDef(attr.name);
        if(def != null) {
            if(ContactAttrDefManager.TYPE_CONTACT.equals(def.type)) {
                try {
                    Integer.parseInt(attr.value);
                }
                catch(Exception e) {
                    Contact contact = ContactManager.getInstance().getContact(attr.value);
                    attr.value = String.valueOf(contact.id);
                }
            }
        }
    }
    
    public ContactAttr getContactAttrFromDB(int id) {
        return this.dao.get(id);
    }
    
    public ContactAttr getContactAttr(int id) {
        ContactAttr attr = this.dao.get(id);
        if(attr != null) {
            return attr;
        }
        else {
            return null;
        }
    }
    
    public List<ContactAttr> getContactAttrs() {
        return this.dao.getAll();
    }
    
    public void updateContactAttr(ContactAttr attr) {
        this.dao.update(attr);
    }
    
    public void deleteContactAttr(int id) {
        this.dao.delete(id);
    }
    
    public List<ContactAttr> getContactAttrsByContactId(int contactId) {
        List<ContactAttr> ret = new ArrayList<ContactAttr>();
        for(ContactAttr attr : this.dao.getAll()) {
            if(attr.contactId == contactId) {
                ret.add(attr);
            }
        }
        Collections.sort(ret, new Comparator<ContactAttr>(){
            @Override
            public int compare(ContactAttr o1, ContactAttr o2) {
                return o1.id - o2.id;
            }
        });
        return ret;
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
        return target instanceof ContactAttr;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ContactAttr attr = (ContactAttr)target;
        return String.valueOf(attr.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    public ContactAttr copyContactAttr(ContactAttr attr) {
        if(attr == null) {
            return null;
        }
        ContactAttr copy = new ContactAttr();
        copy.name = attr.name;
        copy.value = attr.value;
        return copy;
    }
    
    public Object getRealValue(String attrName, String attrValue) {
        ContactAttr attr = new ContactAttr();
        attr.name = attrName;
        attr.value = attrValue;
        return this.getRealValue(attr);
    }
    
    public Object getRealValue(ContactAttr attr) {
        ContactAttrDef def = ContactAttrDefManager.getInstance().getContactAttrDef(attr.name);
        if(def != null) {
            if(ContactAttrDefManager.TYPE_CONTACT.equals(def.type)) {
                try {
                    int id = Integer.parseInt(attr.value);
                    Contact contact = ContactManager.getInstance().getContact(id);
                    if(contact != null) {
                        return contact.name;
                    }
                }
                catch(Exception e) {
                    return attr.value;
                }
            }
        }
        
        return attr.value;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ContactAttr attr = (ContactAttr)target;
        return attr.contactId + "_" + attr.name;
    }
}
