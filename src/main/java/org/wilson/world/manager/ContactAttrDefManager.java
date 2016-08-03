package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Contact;
import org.wilson.world.model.ContactAttr;
import org.wilson.world.model.ContactAttrDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class ContactAttrDefManager implements ItemTypeProvider {
    public static final String NAME = "contact_attr_def";
    
    public static final String TYPE_STRING = "String";
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_INTEGER = "Integer";
    public static final String TYPE_LONG = "Long";
    public static final String TYPE_DOUBLE = "Double";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_DATETIME = "DateTime";
    public static final String TYPE_CONTACT = "Contact";
    
    public static final String DEF_MOBILE = "Mobile";
    public static final String DEF_MAIL = "Mail";
    public static final String DEF_LOCATION = "Location";
    public static final String DEF_BIRTHDAY = "Birthday";
    public static final String DEF_LIKES = "Likes";
    public static final String DEF_DISLIKES = "Dislikes";
    
    private List<String> supported_types = new ArrayList<String>();
    
    private static ContactAttrDefManager instance;
    
    private DAO<ContactAttrDef> dao = null;
    private Cache<String, String> nameTypeCache = null;
    private Cache<Integer, ContactAttrDef> systemDefs = null;
    private int systemID = 1;
    
    @SuppressWarnings("unchecked")
    private ContactAttrDefManager() {
        this.systemDefs = new DefaultCache<Integer, ContactAttrDef>("contact_attr_def_system_def", false);
        
        this.initSupportedTypes();
        this.loadSystemContactAttrDefs();
        
        this.dao = DAOManager.getInstance().getCachedDAO(ContactAttrDef.class);
        this.nameTypeCache = new DefaultCache<String, String>("contact_attr_def_name_type_cache", false);
        this.reloadNameTypeCache();
        
        ((CachedDAO<ContactAttrDef>)this.dao).getCache().addCacheListener(new CacheListener<ContactAttrDef>(){
            @Override
            public void cachePut(ContactAttrDef old, ContactAttrDef v) {
                if(old != null) {
                    ContactAttrDefManager.this.nameTypeCache.delete(old.name);
                }
                ContactAttrDefManager.this.nameTypeCache.put(v.name, v.type);
            }

            @Override
            public void cacheDeleted(ContactAttrDef v) {
                ContactAttrDefManager.this.nameTypeCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<ContactAttrDef> all) {
            }

            @Override
            public void cacheLoading(List<ContactAttrDef> old) {
                reloadNameTypeCache();
            }
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(ContactAttrDef def : getContactAttrDefs()) {
                    boolean found = def.name.contains(text) || def.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = def.id;
                        content.name = def.name;
                        content.description = def.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    private void reloadNameTypeCache() {
        this.nameTypeCache.clear();
        for(ContactAttrDef def : this.systemDefs.getAll()) {
            this.nameTypeCache.put(def.name, def.type);
        }
    }
    
    private void initSupportedTypes() {
        this.supported_types.add(TYPE_STRING);
        this.supported_types.add(TYPE_BOOLEAN);
        this.supported_types.add(TYPE_INTEGER);
        this.supported_types.add(TYPE_LONG);
        this.supported_types.add(TYPE_DOUBLE);
        this.supported_types.add(TYPE_DATE);
        this.supported_types.add(TYPE_DATETIME);
    }
    
    private void addSystemContactAttrDef(ContactAttrDef def) {
        this.systemDefs.put(def.id, def);
    }
    
    private void loadSystemContactAttrDefs() {
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_MOBILE, TYPE_STRING, "This attribute modifies the mobile of the contact.", true));
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_MAIL, TYPE_STRING, "This attribute modifies the mail of the contact.", true));
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_LOCATION, TYPE_STRING, "This attribute modifies the location of the contact.", true));
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_BIRTHDAY, TYPE_DATE, "This attribute modifies the birthday of the contact.", true));
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_LIKES, TYPE_STRING, "This attribute modifies the likes of the contact.", true));
        addSystemContactAttrDef(this.buildContactAttrDef(DEF_DISLIKES, TYPE_STRING, "This attribute modifies the dislikes of the contact.", true));
    }
    
    public ContactAttrDef buildContactAttrDef(String name, String type, String description, boolean isSystem) {
        ContactAttrDef def = new ContactAttrDef();
        def.name = name;
        def.type = type;
        def.description = description;
        def.isSystem = isSystem;
        if(def.isSystem) {
            def.id = -this.systemID++;
        }
        return def;
    }
    
    public ContactAttrDef buildContactAttrDef(String name, String type, String description) {
        return this.buildContactAttrDef(name, type, description, false);
    }
    
    public static ContactAttrDefManager getInstance() {
        if(instance == null) {
            instance = new ContactAttrDefManager();
        }
        return instance;
    }
    
    public void createContactAttrDef(ContactAttrDef def) {
        if(this.nameTypeCache.get(def.name) != null) {
            throw new DataException("Contact attr def with name [" + def.name + "] already exists!");
        }
        
        this.dao.create(def);
    }
    
    public ContactAttrDef getContactAttrDef(int id) {
        ContactAttrDef def = this.systemDefs.get(id);
        if(def != null) {
            return def;
        }
        else {
            def = this.dao.get(id);
            if(def != null) {
                return def;
            }
            else {
                return null;
            }
        }
    }
    
    public ContactAttrDef getContactAttrDef(String name) {
        for(ContactAttrDef def : this.getContactAttrDefs()) {
            if(def.name.equals(name)) {
                return def;
            }
        }
        return null;
    }
    
    public List<ContactAttrDef> getContactAttrDefs() {
        List<ContactAttrDef> result = new ArrayList<ContactAttrDef>();
        for(ContactAttrDef def : this.systemDefs.getAll()) {
            result.add(def);
        }
        for(ContactAttrDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public List<String> getContactAttrNames() {
        return this.nameTypeCache.getKeys();
    }
    
    public String getContactAttrType(String name) {
        return this.nameTypeCache.get(name);
    }
    
    public void updateContactAttrDef(ContactAttrDef def) {
        this.dao.update(def);
    }
    
    public void deleteContactAttrDef(int id) {
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
        return target instanceof ContactAttrDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ContactAttrDef def = (ContactAttrDef)target;
        return String.valueOf(def.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<String> getSupportedTypes() {
        return this.supported_types;
    }
    
    public boolean isValidContactAttrName(String name) {
        return this.nameTypeCache.get(name) != null;
    }
    
    public boolean isContactAttrDefUsed(String name) {
        if(StringUtils.isBlank(name)) {
            return false;
        }
        if(!this.isValidContactAttrName(name)) {
            return false;
        }
        List<Contact> contacts = ContactManager.getInstance().getContacts();
        for(Contact contact : contacts) {
            ContactAttr attr = ContactManager.getInstance().getContactAttr(contact, name);
            if(attr != null && !StringUtils.isBlank(attr.value)) {
                return true;
            }
        }
        
        return false;
    }
}
