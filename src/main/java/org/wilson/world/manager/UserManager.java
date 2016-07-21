package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.model.User;

public class UserManager {
    private static UserManager instance;
    
    private Cache<String, User> cache = null;
    private DAO<User> dao = null;
    
    @SuppressWarnings("unchecked")
    private UserManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(User.class);
        
        cache = new DefaultCache<String, User>("user_manager_cache", false);
        if(this.dao instanceof CachedDAO) {
            ((CachedDAO<User>)this.dao).getCache().addCacheListener(new CacheListener<User>(){
                @Override
                public void cachePut(User old, User v) {
                    if(old != null) {
                        UserManager.this.cache.delete(old.username);
                    }
                    UserManager.this.cache.put(v.username, v);
                }

                @Override
                public void cacheDeleted(User v) {
                    UserManager.this.cache.delete(v.username);
                }

                @Override
                public void cacheLoaded(List<User> all) {
                    for(User user : all) {
                        cachePut(null, user);
                    }
                }

                @Override
                public void cacheLoading(List<User> old) {
                    UserManager.this.cache.clear();
                }
            });
        }
    }
    
    public static UserManager getInstance() {
        if(instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    public User getUser(String username) {
        if(username == null) {
            return null;
        }
        return this.cache.get(username);
    }
}
