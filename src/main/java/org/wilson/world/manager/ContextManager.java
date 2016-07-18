package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Context;

public class ContextManager implements ItemTypeProvider {
    public static final String NAME = "context";
    
    public static final String CONTEXT_WORK = "Work";
    public static final String CONTEXT_LEISURE = "Leisure";
    
    private Context currentContext;
    
    private static ContextManager instance;
    
    private DAO<Context> dao = null;
    private Cache<String, Context> contextCache = null;
    
    @SuppressWarnings("unchecked")
    private ContextManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Context.class);
        this.contextCache = new DefaultCache<String, Context>("context_manager_context_cache", false);
        ((CachedDAO<Context>)this.dao).getCache().addCacheListener(new CacheListener<Context>(){
            @Override
            public void cachePut(Context v) {
                ContextManager.this.contextCache.put(v.name, v);
            }

            @Override
            public void cacheDeleted(Context v) {
                ContextManager.this.contextCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<Context> all) {
                for(Context context : all) {
                    cachePut(context);
                }
            }

            @Override
            public void cacheLoading(List<Context> old) {
                ContextManager.this.contextCache.clear();
            }
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static ContextManager getInstance() {
        if(instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }
    
    public void setCurrentContext(String name) {
        if(StringUtils.isBlank(name)) {
            return;
        }
        Context context = this.getContext(name);
        if(context != null) {
            this.setCurrentContext(context);
        }
    }
    
    public void setCurrentContext(Context context) {
        this.currentContext = context;
    }
    
    public Context getCurrentContext() {
        return this.currentContext;
    }
    
    public void createContext(Context context) {
        this.dao.create(context);
    }
    
    public Context getContext(int id) {
        Context context = this.dao.get(id);
        if(context != null) {
            return context;
        }
        else {
            return null;
        }
    }
    
    public Context getContext(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        return this.contextCache.get(name);
    }
    
    public List<Context> getContexts() {
        List<Context> result = new ArrayList<Context>();
        for(Context context : this.dao.getAll()) {
            result.add(context);
        }
        return result;
    }
    
    public void updateContext(Context context) {
        this.dao.update(context);
    }
    
    public void deleteContext(int id) {
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
        return target instanceof Context;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Context context = (Context)target;
        return String.valueOf(context.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
}
