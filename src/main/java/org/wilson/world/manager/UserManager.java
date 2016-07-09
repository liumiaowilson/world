package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.db.DBUtils;
import org.wilson.world.model.User;

public class UserManager implements CacheProvider {
    private static final Logger logger = Logger.getLogger(UserManager.class);
    
    private static UserManager instance;
    
    private Map<String, User> cache = null;
    
    private UserManager() {
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
        return "user";
    }

    @Override
    public boolean canPreload() {
        return true;
    }

    @Override
    public void reloadCache() {
        cache = new HashMap<String, User>();
        
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            String sql = "select * from users;";
            rs = s.executeQuery(sql);
            while(rs.next()) {
                String username = rs.getString(2);
                String password = rs.getString(3);
                User user = new User();
                user.username = username;
                user.password = password;
                cache.put(user.username, user);
            }
        }
        catch(Exception e) {
            logger.error("failed to load users!", e);
        }
        finally {
            DBUtils.closeQuietly(con, s, rs);
        }
    }
}
