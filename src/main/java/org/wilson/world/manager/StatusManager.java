package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Status;
import org.wilson.world.status.DefaultStatus;
import org.wilson.world.status.IStatus;
import org.wilson.world.status.SkilledStatus;
import org.wilson.world.status.StatusActivator;
import org.wilson.world.status.StatusDeactivator;
import org.wilson.world.status.SystemStatus;

public class StatusManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(StatusManager.class);
    
    private static int GLOBAL_ID = 1;
    
    public static final String NAME = "status";
    
    private static StatusManager instance;
    
    private DAO<Status> dao = null;
    
    private Cache<Integer, IStatus> cache = null;
    private Cache<String, IStatus> nameCache = null;
    
    @SuppressWarnings("unchecked")
    private StatusManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Status.class);
        this.cache = new DefaultCache<Integer, IStatus>("status_manager_cache", false);
        this.nameCache = new DefaultCache<String, IStatus>("status_manager_name_cache", false);
        
        ((CachedDAO<Status>)this.dao).getCache().addCacheListener(new CacheListener<Status>(){

            @Override
            public void cachePut(Status old, Status v) {
                if(old != null) {
                    StatusManager.this.cache.delete(old.id);
                    StatusManager.this.nameCache.delete(old.name);
                }
                loadStatus(v);
            }

            @Override
            public void cacheDeleted(Status v) {
                StatusManager.this.cache.delete(v.id);
                StatusManager.this.nameCache.delete(v.name);
            }

            @Override
            public void cacheLoaded(List<Status> all) {
                loadSystemStatuses();
            }

            @Override
            public void cacheLoading(List<Status> old) {
                StatusManager.this.cache.clear();
                StatusManager.this.nameCache.clear();
            }
            
        });
        
        ItemManager.getInstance().registerItemTypeProvider(this);
    }
    
    public static StatusManager getInstance() {
        if(instance == null) {
            instance = new StatusManager();
        }
        return instance;
    }
    
    private void loadSystemStatuses() {
        GLOBAL_ID = 1;
        
        this.loadSystemStatus(new SkilledStatus());
    }
    
    private void loadSystemStatus(SystemStatus status) {
        if(status != null) {
            status.setID(-GLOBAL_ID++);
            this.cache.put(status.getID(), status);
            this.nameCache.put(status.getName(), status);
        }
    }
    
    private void loadStatus(Status status) {
        if(status != null) {
            StatusActivator activator = this.loadStatusActivator(status);
            if(activator == null) {
                return;
            }
            
            StatusDeactivator deactivator = this.loadStatusDeactivator(status);
            if(deactivator == null) {
                return;
            }
            
            DefaultStatus ret = new DefaultStatus(status, activator, deactivator);
            this.cache.put(ret.getID(), ret);
            this.nameCache.put(ret.getName(), ret);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private StatusActivator loadStatusActivator(Status status) {
        if(status == null) {
            return null;
        }
        
        String impl = status.activator;
        if(StringUtils.isBlank(impl)) {
            return null;
        }
        
        StatusActivator ret = null;
        try {
            Class clazz = Class.forName(impl);
            ret = (StatusActivator) clazz.newInstance();
            logger.info("Loaded status activator using class [" + impl + "]");
        }
        catch(Exception e) {
            ret = (StatusActivator) ExtManager.getInstance().wrapAction(impl, StatusActivator.class);
            if(ret == null) {
                logger.warn("Failed to load status activator using [" + impl + "]");
                return null;
            }
            else {
                logger.info("Loaded status activator using action [" + impl + "]");
            }
        }
        
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    private StatusDeactivator loadStatusDeactivator(Status status) {
        if(status == null) {
            return null;
        }
        
        String impl = status.deactivator;
        if(StringUtils.isBlank(impl)) {
            return null;
        }
        
        StatusDeactivator ret = null;
        try {
            Class clazz = Class.forName(impl);
            ret = (StatusDeactivator) clazz.newInstance();
            logger.info("Loaded status deactivator using class [" + impl + "]");
        }
        catch(Exception e) {
            ret = (StatusDeactivator) ExtManager.getInstance().wrapAction(impl, StatusDeactivator.class);
            if(ret == null) {
                logger.warn("Failed to load status deactivator using [" + impl + "]");
                return null;
            }
            else {
                logger.info("Loaded status deactivator using action [" + impl + "]");
            }
        }
        
        return ret;
    }
    
    public void createStatus(Status status) {
        this.dao.create(status);
    }
    
    public Status getStatus(int id) {
        Status status = this.dao.get(id);
        if(status != null) {
            return status;
        }
        else {
            return null;
        }
    }
    
    public List<Status> getStatuses() {
        List<Status> result = new ArrayList<Status>();
        for(Status status : this.dao.getAll()) {
            result.add(status);
        }
        return result;
    }
    
    public void updateStatus(Status status) {
        this.dao.update(status);
    }
    
    public void deleteStatus(int id) {
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
        return target instanceof Status;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Status status = (Status)target;
        return String.valueOf(status.id);
    }

    @Override
    public int getItemCount() {
        return this.dao.getAll().size();
    }
    
    public List<IStatus> getIStatuses() {
        return this.cache.getAll();
    }
    
    public IStatus getIStatus(int id) {
        return this.cache.get(id);
    }
    
    public IStatus getIStatus(String name) {
        return this.nameCache.get(name);
    }
    
    public List<IStatus> getSystemStatuses() {
        List<IStatus> ret = new ArrayList<IStatus>();
        
        for(IStatus status : this.getIStatuses()) {
            if(status instanceof SystemStatus) {
                ret.add(status);
            }
        }
        
        return ret;
    }
    
    public IStatus randomStatus() {
        List<IStatus> all = this.getIStatuses();
        if(all.isEmpty()) {
            return null;
        }
        int idx = DiceManager.getInstance().random(all.size());
        return all.get(idx);
    }
}
