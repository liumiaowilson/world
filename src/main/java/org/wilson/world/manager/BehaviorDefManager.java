package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.behavior.BehaviorDefIdeaConverter;
import org.wilson.world.behavior.DefaultBehaviorDef;
import org.wilson.world.behavior.IBehaviorDef;
import org.wilson.world.behavior.SystemBehaviorDef;
import org.wilson.world.behavior.SystemBehaviorDefProvider;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.idea.IdeaConverterFactory;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Behavior;
import org.wilson.world.model.BehaviorDef;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class BehaviorDefManager implements ItemTypeProvider, EventListener, ManagerLifecycle {
    public static final String NAME = "behavior_def";
    
    private static BehaviorDefManager instance;
    
    private DAO<BehaviorDef> dao = null;
    
    private List<SystemBehaviorDefProvider> providers = new ArrayList<SystemBehaviorDefProvider>();
    
    private Cache<Integer, IBehaviorDef> defs = null;
    
    @SuppressWarnings("unchecked")
    private BehaviorDefManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(BehaviorDef.class);
        this.defs = new DefaultCache<Integer, IBehaviorDef>("behavior_def_manager_defs", false);
        ((CachedDAO<BehaviorDef>)this.dao).getCache().addCacheListener(new CacheListener<BehaviorDef>(){

            @Override
            public void cachePut(BehaviorDef old, BehaviorDef v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                loadBehaviorDef(v);
            }

            @Override
            public void cacheDeleted(BehaviorDef v) {
                BehaviorDefManager.this.defs.delete(v.id);
            }

            @Override
            public void cacheLoaded(List<BehaviorDef> all) {
                loadSystemBehaviorDefs();
            }

            @Override
            public void cacheLoading(List<BehaviorDef> old) {
                BehaviorDefManager.this.defs.clear();
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
        
        IdeaConverterFactory.getInstance().addIdeaConverter(new BehaviorDefIdeaConverter());
    }
    
    public static BehaviorDefManager getInstance() {
        if(instance == null) {
            instance = new BehaviorDefManager();
        }
        return instance;
    }
    
    private void loadSystemBehaviorDefs() {
        for(SystemBehaviorDefProvider provider : this.providers) {
            SystemBehaviorDef def = provider.getSystemBehaviorDef();
            this.defs.put(def.getId(), def);
        }
    }
    
    private void loadBehaviorDef(BehaviorDef def) {
        if(def != null) {
            DefaultBehaviorDef dbd = new DefaultBehaviorDef(def);
            this.defs.put(dbd.getId(), dbd);
        }
    }
    
    public void addSystemBehaviorDefProvider(SystemBehaviorDefProvider provider) {
        if(provider != null) {
            this.providers.add(provider);
        }
    }
    
    public void removeSystemBehaviorDefProvider(SystemBehaviorDefProvider provider) {
        if(provider != null) {
            this.providers.remove(provider);
        }
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
    
    public IBehaviorDef getIBehaviorDef(int id) {
        return this.defs.get(id);
    }
    
    public List<IBehaviorDef> getIBehaviorDefs() {
        return this.defs.getAll();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void handle(Event event) {
        for(SystemBehaviorDefProvider provider : this.providers) {
            if(provider.getEventType() == event.type) {
                SystemBehaviorDef def = provider.getSystemBehaviorDef();
                
                Behavior behavior = new Behavior();
                behavior.defId = def.getId();
                behavior.time = System.currentTimeMillis();
                BehaviorManager.getInstance().createBehavior(behavior);
                
                Event newEvent = new Event();
                newEvent.type = EventType.CreateBehavior;
                newEvent.data.put("data", behavior);
                EventManager.getInstance().fireEvent(newEvent);
            }
        }
    }

    @Override
    public void start() {
        for(SystemBehaviorDefProvider provider : this.providers) {
            EventManager.getInstance().registerListener(provider.getEventType(), this);
        }
    }

    @Override
    public void shutdown() {
    }
}
