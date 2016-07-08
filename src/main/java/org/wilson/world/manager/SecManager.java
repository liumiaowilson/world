package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;

public class SecManager {
    private static final Logger logger = Logger.getLogger(SecManager.class);
    
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
    
    public String authenticate(String username, String password) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from users where username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if(rs.next()) {
                String pwd = rs.getString(3);
                if(pwd.equals(password)) {
                    return null;
                }
                else {
                    return "Invalid username and password.";
                }
            }
            else {
                return "User does not exist.";
            }
        }
        catch(Exception e) {
            logger.error("failed to authenticate!", e);
            return "Failed to authenticate!";
        }
        finally{ 
            DBUtils.closeQuietly(con, ps, rs);
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
