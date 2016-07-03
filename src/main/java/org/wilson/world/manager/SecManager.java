package org.wilson.world.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class SecManager {
    private static SecManager instance = null;
    
    private static final long TIMEOUT_DURATION = 24 * 60 * 60 * 1000;
    
    private Map<String, Long> tokens = new HashMap<String, Long>();
    
    private SecManager() {}
    
    public static SecManager getInstance() {
        if(instance == null) {
            instance = new SecManager();
        }
        return instance;
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
