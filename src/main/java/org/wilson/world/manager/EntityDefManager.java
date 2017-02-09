package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.EntityDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.util.IOUtils;

public class EntityDefManager implements ItemTypeProvider {
    public static final String NAME = "entity_def";
    
    private static EntityDefManager instance;
    
    private DAO<EntityDef> dao = null;
    
    private String defaultContent = null;
    
    @SuppressWarnings("unchecked")
    private EntityDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(EntityDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(EntityDef def : getEntityDefs()) {
                    boolean found = def.name.contains(text) || def.def.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = def.id;
                        content.name = def.name;
                        content.description = def.def;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static EntityDefManager getInstance() {
        if(instance == null) {
            instance = new EntityDefManager();
        }
        return instance;
    }
    
    public void createEntityDef(EntityDef def) {
        ItemManager.getInstance().checkDuplicate(def);
        
        this.dao.create(def);
    }
    
    public EntityDef getEntityDef(int id) {
    	EntityDef def = this.dao.get(id);
        if(def != null) {
            return def;
        }
        else {
            return null;
        }
    }
    
    public List<EntityDef> getEntityDefs() {
        List<EntityDef> result = new ArrayList<EntityDef>();
        for(EntityDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public void updateEntityDef(EntityDef def) {
        this.dao.update(def);
    }
    
    public void deleteEntityDef(int id) {
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
        return target instanceof EntityDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        EntityDef def = (EntityDef)target;
        return String.valueOf(def.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        EntityDef def = (EntityDef)target;
        return def.name;
    }
    
    public String getDefaultContent() throws IOException {
        if(defaultContent == null) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("entity_def.json");
            defaultContent = IOUtils.toString(is);
        }
        return this.defaultContent;
    }
}
