package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTableInfo;
import org.wilson.world.item.ItemTypeProvider;

public class ItemManager {
    private static final Logger logger = Logger.getLogger(ItemManager.class);
    
    private static ItemManager instance;
    
    private List<ItemTypeProvider> providers = new ArrayList<ItemTypeProvider>();
    
    private ItemManager() {
        this.registerItemTypeProvider(IdeaManager.getInstance());
    }
    
    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }
    
    public void registerItemTypeProvider(ItemTypeProvider provider) {
        if(provider != null && !this.providers.contains(provider)) {
            this.providers.add(provider);
        }
    }
    
    public void unregisterItemTypeProvider(ItemTypeProvider provider) {
        if(provider != null && this.providers.contains(provider)) {
            this.providers.remove(provider);
        }
    }
    
    public List<String> getItemTableNames() {
        List<String> result = new ArrayList<String>();
        for(ItemTypeProvider provider : providers) {
            result.add(provider.getItemTableName());
        }
        return result;
    }
    
    public Map<String, ItemTableInfo> getItemTableInfos() {
        List<String> names = getItemTableNames();
        
        Map<String, ItemTableInfo> result = new HashMap<String, ItemTableInfo>();
        Connection con = DBUtils.getConnection();
        try {
            for(String tableName : names) {
                String sql = "select count(*) from " + tableName + ";";
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(sql);
                if(rs.next()) {
                    int count = rs.getInt(1);
                    
                    ItemTableInfo info = new ItemTableInfo();
                    info.tableName = tableName;
                    info.rowCount = count;
                    
                    result.put(tableName, info);
                }
                
                DBUtils.closeQuietly(null, rs);
            }
        }
        catch(Exception e) {
            logger.error("failed to get item table infos!", e);
            throw new DataException("failed to get item table infos!");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
        
        return result;
    }
    
    public void clearTables(List<String> tableNames) {
        List<String> names = this.getItemTableNames();
        
        Connection con = DBUtils.getConnection();
        try {
            for(String tableName : tableNames) {
                if(names.contains(tableName)) {
                    String sql = "delete from " + tableName + ";";
                    Statement s = con.createStatement();
                    s.execute(sql);
                }
            }
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            throw new DataException("failed to clear table!");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }
    
    public void clearTable(String tableName) {
        if(StringUtils.isBlank(tableName)) {
            return;
        }
        
        List<String> names = this.getItemTableNames();
        if(!names.contains(tableName)) {
            throw new DataException("Table name is invalid.");
        }
        
        Connection con = DBUtils.getConnection();
        try {
            String sql = "delete from " + tableName + ";";
            Statement s = con.createStatement();
            s.execute(sql);
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            throw new DataException("failed to clear table!");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }
    
    public String getItemTypeName(Object target) {
        if(target == null) {
            return null;
        }
        
        for(ItemTypeProvider provider : this.providers) {
            if(provider.accept(target)) {
                return provider.getItemTypeName();
            }
        }
        
        return null;
    }
    
    public String getItemID(Object target) {
        if(target == null) {
            return null;
        }
        
        for(ItemTypeProvider provider : this.providers) {
            if(provider.accept(target)) {
                return provider.getID(target);
            }
        }
        
        return null;
    }
}
