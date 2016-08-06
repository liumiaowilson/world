package org.wilson.world.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.model.User;

public class SecManager {
    private static SecManager instance = null;
    
    private static final long TIMEOUT_DURATION = 24 * 60 * 60 * 1000;
    
    private Map<String, Long> tokens = new HashMap<String, Long>();
    
    private boolean locked = false;
    private int failLimit;
    private int failCount = 0;
    
    private SecManager() {
        this.failLimit = ConfigManager.getInstance().getConfigAsInt("login.fail.lock.count", 3);
    }
    
    public static SecManager getInstance() {
        if(instance == null) {
            instance = new SecManager();
        }
        return instance;
    }
    
    public String authenticate(String username, String password) {
        if(this.locked) {
            return "System has been locked.";
        }
        User user = UserManager.getInstance().getUser(username);
        if(user == null || (user.password != null && !user.password.equals(password))) {
            this.failCount += 1;
            if(this.failCount >= this.failLimit) {
                this.locked = true;
                return "System has been locked.";
            }
            else {
                return "Username or password is invalid.";
            }
        }
        else {
            this.failCount = 0;
            UserManager.getInstance().setCurrentUser(user);
            return null;
        }
    }
    
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    public void addToken(String token) {
        if(!StringUtils.isBlank(token)) {
            this.tokens.put(token, System.currentTimeMillis());
        }
    }

    public boolean isValidToken(String token) {
        if(!this.tokens.containsKey(token)) {
            return false;
        }
        long time = tokens.get(token);
        if(time + TIMEOUT_DURATION < System.currentTimeMillis()) {
            this.tokens.remove(token);
            return false;
        }
        else {
            return true;
        }
    }
}
