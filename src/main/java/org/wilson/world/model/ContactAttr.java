package org.wilson.world.model;

public class ContactAttr {
    public int id;
    
    public int contactId;
    
    public String name;
    
    public String value;
    
    public static ContactAttr create(String name, String value) {
        ContactAttr attr = new ContactAttr();
        attr.name = name;
        attr.value = value;
        return attr;
    }
}
