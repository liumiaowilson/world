package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTableInfo;
import org.wilson.world.item.ItemTypeProvider;

public class ItemManager {
    private static final Logger logger = Logger.getLogger(ItemManager.class);
    
    private static ItemManager instance;
    
    private List<ItemTypeProvider> providers = new ArrayList<ItemTypeProvider>();
    
    private ItemManager() {
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
        Collections.sort(result);
        return result;
    }
    
    public Map<String, ItemTableInfo> getItemTableInfos() {
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            return this.getItemTableInfosFromDB();
        }
        else {
            return this.getItemTableInfosFromMemory();
        }
    }
    
    private Map<String, ItemTableInfo> getItemTableInfosFromMemory() {
        Map<String, ItemTableInfo> result = new HashMap<String, ItemTableInfo>();
        for(ItemTypeProvider provider : this.providers) {
            ItemTableInfo info = new ItemTableInfo();
            info.tableName = provider.getItemTableName();
            info.rowCount = provider.getDAO().getAll().size();
            result.put(info.tableName, info);
        }
        return result;
    }
    
    private Map<String, ItemTableInfo> getItemTableInfosFromDB() {
        List<String> names = getItemTableNames();
        
        Map<String, ItemTableInfo> result = new HashMap<String, ItemTableInfo>();
        if(!ConfigManager.getInstance().isInMemoryMode()) {
            
        }
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            for(String tableName : names) {
                String sql = "select count(*) from " + tableName + ";";
                rs = s.executeQuery(sql);
                if(rs.next()) {
                    int count = rs.getInt(1);
                    
                    ItemTableInfo info = new ItemTableInfo();
                    info.tableName = tableName;
                    info.rowCount = count;
                    
                    result.put(tableName, info);
                }
                
                DBUtils.closeQuietly(null, null, rs);
            }
        }
        catch(Exception e) {
            logger.error("failed to get item table infos!", e);
            throw new DataException("failed to get item table infos!");
        }
        finally {
            DBUtils.closeQuietly(con, s, null);
        }
        
        return result;
    }
    
    public void clearTables(List<String> tableNames) {
        List<String> names = this.getItemTableNames();
        
        Connection con = null;
        Statement s = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            for(String tableName : tableNames) {
                if(names.contains(tableName)) {
                    String sql = "delete from " + tableName + ";";
                    s.execute(sql);
                }
            }
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            throw new DataException("failed to clear table!");
        }
        finally {
            DBUtils.closeQuietly(con, s, null);
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
        
        Connection con = null;
        Statement s = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from " + tableName + ";";
            s = con.createStatement();
            s.execute(sql);
        }
        catch(Exception e) {
            logger.error("failed to clear table!", e);
            throw new DataException("failed to clear table!");
        }
        finally {
            DBUtils.closeQuietly(con, s, null);
        }
    }
    
    public String getItemTypeName(Object target) {
        ItemTypeProvider provider = this.getItemTypeProvider(target);
        if(provider != null) {
            return provider.getItemTypeName();
        }
        else {
            return null;
        }
    }
    
    public String getItemID(Object target) {
        ItemTypeProvider provider = this.getItemTypeProvider(target);
        if(provider != null) {
            return provider.getID(target);
        }
        else {
            return null;
        }
    }
    
    public ItemTypeProvider getItemTypeProviderByItemTableName(String itemTableName) {
        if(itemTableName == null) {
            return null;
        }
        for(ItemTypeProvider provider : this.providers) {
            if(itemTableName.equals(provider.getItemTableName())) {
                return provider;
            }
        }
        return null;
    }
    
    public ItemTypeProvider getItemTypeProvider(Object target) {
        if(target == null) {
            return null;
        }
        for(ItemTypeProvider provider : this.providers) {
            if(provider.accept(target)) {
                return provider;
            }
        }
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    public void checkDuplicate(Object target) {
        if(target == null) {
            return;
        }
        ItemTypeProvider provider = this.getItemTypeProvider(target);
        if(provider == null) {
            return;
        }
        
        String identifier = provider.getIdentifier(target);
        if(identifier == null) {
            return;
        }
        
        DAO dao = provider.getDAO();
        if(dao == null) {
            return;
        }
        
        for(Object obj : dao.getAll()) {
            String objIdentifier = provider.getIdentifier(obj);
            if(identifier.equals(objIdentifier)) {
                throw new DataException("Duplicated items detected.");
            }
        }
    }
}
