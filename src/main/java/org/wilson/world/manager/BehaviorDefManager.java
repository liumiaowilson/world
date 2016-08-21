package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.BehaviorDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class BehaviorDefManager implements ItemTypeProvider {
    public static final String NAME = "behavior_def";
    
    private static BehaviorDefManager instance;
    
    private DAO<BehaviorDef> dao = null;
    
    @SuppressWarnings("unchecked")
    private BehaviorDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(BehaviorDef.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(BehaviorDef def : getBehaviorDefs()) {
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
    
    public static BehaviorDefManager getInstance() {
        if(instance == null) {
            instance = new BehaviorDefManager();
        }
        return instance;
    }
    
    public void createBehaviorDef(BehaviorDef def) {
        ItemManager.getInstance().checkDuplicate(def);
        
        this.dao.create(def);
    }
    
    public BehaviorDef getBehaviorDef(int id) {
        BehaviorDef def = this.dao.get(id);
        if(def != null) {
            return def;
        }
        else {
            return null;
        }
    }
    
    public List<BehaviorDef> getBehaviorDefs() {
        List<BehaviorDef> result = new ArrayList<BehaviorDef>();
        for(BehaviorDef def : this.dao.getAll()) {
            result.add(def);
        }
        return result;
    }
    
    public void updateBehaviorDef(BehaviorDef def) {
        this.dao.update(def);
    }
    
    public void deleteBehaviorDef(int id) {
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
        return target instanceof BehaviorDef;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        BehaviorDef def = (BehaviorDef)target;
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
        
        BehaviorDef def = (BehaviorDef)target;
        return def.name;
    }
}
