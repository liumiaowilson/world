package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Alias;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class AliasManager implements ItemTypeProvider {
    public static final String NAME = "alias";
    
    private static AliasManager instance;
    
    private DAO<Alias> dao = null;
    
    private Map<String, Set<String>> aliases = new HashMap<String, Set<String>>();
    
    @SuppressWarnings("unchecked")
    private AliasManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Alias.class);
        ((CachedDAO<Alias>)this.dao).getCache().addCacheListener(new CacheListener<Alias>(){

            @Override
            public void cachePut(Alias old, Alias v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                addAlias(v);
            }

            @Override
            public void cacheDeleted(Alias v) {
                removeAlias(v);
            }

            @Override
            public void cacheLoaded(List<Alias> all) {
            }

            @Override
            public void cacheLoading(List<Alias> old) {
                AliasManager.this.aliases.clear();
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
                
                for(Alias alias : getAliases()) {
                    boolean found = alias.name.contains(text) || alias.content.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = alias.id;
                        content.name = alias.name;
                        content.description = alias.content;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static AliasManager getInstance() {
        if(instance == null) {
            instance = new AliasManager();
        }
        return instance;
    }
    
    public void createAlias(Alias alias) {
        ItemManager.getInstance().checkDuplicate(alias);
        
        this.dao.create(alias);
    }
    
    private Set<String> toSet(Alias alias) {
        if(alias == null) {
            return Collections.emptySet();
        }
        
        Set<String> ret = new HashSet<String>();
        ret.add(alias.name);
        
        for(String item : alias.content.trim().split(",")) {
            item = item.trim();
            if(!StringUtils.isBlank(item)) {
                ret.add(item);
            }
        }
        
        return ret;
    }
    
    private void addAlias(Alias alias) {
        Set<String> set = this.toSet(alias);
        Set<String> old = null;
        for(String item : set) {
            old = this.aliases.get(item);
            if(old != null) {
                break;
            }
        }
        
        if(old == null) {
            old = new HashSet<String>();
        }
        
        for(String item : set) {
            old.add(item);
            this.aliases.put(item, old);
        }
    }
    
    private void removeAlias(Alias alias) {
        Set<String> set = this.toSet(alias);
        
        for(String item : set) {
            this.aliases.remove(item);
        }
        
        //some remaining here
    }
    
    public Alias getAlias(int id) {
        Alias alias = this.dao.get(id);
        if(alias != null) {
            return alias;
        }
        else {
            return null;
        }
    }
    
    public List<Alias> getAliases() {
        List<Alias> result = new ArrayList<Alias>();
        for(Alias alias : this.dao.getAll()) {
            result.add(alias);
        }
        return result;
    }
    
    public void updateAlias(Alias alias) {
        this.dao.update(alias);
    }
    
    public void deleteAlias(int id) {
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
        return target instanceof Alias;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Alias alias = (Alias)target;
        return String.valueOf(alias.id);
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
        
        Alias alias = (Alias)target;
        return alias.name;
    }
    
    public Set<String> getAliases(String name) {
        Set<String> ret = this.aliases.get(name);
        if(ret == null) {
            return Collections.emptySet();
        }
        
        return ret;
    }
}
