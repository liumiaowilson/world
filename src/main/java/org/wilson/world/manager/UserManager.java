package org.wilson.world.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.CacheProvider;
import org.wilson.world.dao.DAO;
import org.wilson.world.model.User;

public class UserManager implements CacheProvider {
    private static UserManager instance;
    
    private Map<String, User> cache = null;
    private DAO<User> dao = null;
    
    @SuppressWarnings("unchecked")
    private UserManager() {
        this.dao = DAOManager.getInstance().getDAO(User.class);
        
        CacheManager.getInstance().registerCacheProvider(this);
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
        return getUsers().get(username);
    }
    
    private Map<String, User> getUsers() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    @Override
    public String getCacheProviderName() {
        return this.dao.getItemTableName();
    }

    @Override
    public boolean canPreload() {
        return true;
    }

    @Override
    public void reloadCache() {
        cache = new HashMap<String, User>();
        
        List<User> users = this.dao.getAll();
        for(User user : users) {
            cache.put(user.username, user);
        }
    }
}
