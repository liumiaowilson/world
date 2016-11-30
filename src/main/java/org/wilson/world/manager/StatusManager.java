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
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.model.Status;
import org.wilson.world.status.DefaultStatus;
import org.wilson.world.status.IStatus;
import org.wilson.world.status.SkilledStatus;
import org.wilson.world.status.StatusActivator;
import org.wilson.world.status.StatusDeactivator;
import org.wilson.world.status.SystemStatus;

public class StatusManager implements ItemTypeProvider, JavaExtensionListener<SystemStatus> {
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
        
        ExtManager.getInstance().addJavaExtensionListener(this);
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
    
    private void removeSystemStatus(SystemStatus status) {
    	if(status != null) {
    		this.cache.delete(status.getID());
    		this.nameCache.delete(status.getName());
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
    
    private StatusActivator loadStatusActivator(Status status) {
        if(status == null) {
            return null;
        }
        
        String impl = status.activator;
        if(StringUtils.isBlank(impl)) {
            return null;
        }
        
        StatusActivator ret = (StatusActivator) ExtManager.getInstance().getExtension(impl, StatusActivator.class);
        
        return ret;
    }
    
    private StatusDeactivator loadStatusDeactivator(Status status) {
        if(status == null) {
            return null;
        }
        
        String impl = status.deactivator;
        if(StringUtils.isBlank(impl)) {
            return null;
        }
        
        StatusDeactivator ret = (StatusDeactivator) ExtManager.getInstance().getExtension(impl, StatusDeactivator.class);
        
        return ret;
    }
    
    public void createStatus(Status status) {
        ItemManager.getInstance().checkDuplicate(status);
        
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

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
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
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Status status = (Status)target;
        return status.name;
    }

	@Override
	public Class<SystemStatus> getExtensionClass() {
		return SystemStatus.class;
	}

	@Override
	public void created(SystemStatus t) {
		this.loadSystemStatus(t);
	}

	@Override
	public void removed(SystemStatus t) {
		this.removeSystemStatus(t);
	}
}
