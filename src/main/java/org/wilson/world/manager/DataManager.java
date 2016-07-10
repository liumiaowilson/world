package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.DataItem;

public class DataManager implements CacheProvider {
    private static final Logger logger = Logger.getLogger(DataManager.class);
    
    public static final String NAME = "data";
    
    private static DataManager instance;
    
    private Map<Integer, DataItem> cache;
    private Map<String, String> dataCache;
    
    private DataManager() {
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private Map<Integer, DataItem> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    private Map<String, String> getDataCache() {
        if(dataCache == null) {
            this.reloadCache();
        }
        return dataCache;
    }
    
    private void validateDataItem(DataItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(item.name == null || item.name.length() > 20) {
            throw new DataException("Item name is invalid");
        }
        if(item.value == null || item.value.length() > 100) {
            throw new DataException("Item value is invalid");
        }
    }
    
    public void createDataItem(DataItem item) {
        validateDataItem(item);
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into data(name, value) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.name);
            ps.setString(2, item.value);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                item.id = id;
            }
            
            this.getCache().put(item.id, item);
            this.getDataCache().put(item.name, item.value);
        }
        catch(Exception e) {
            logger.error("failed to create data item", e);
            throw new DataException("failed to create data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public void updateDataItem(DataItem item) {
        validateDataItem(item);
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update data set name = ?, value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, item.name);
            ps.setString(2, item.value);
            ps.setInt(3, item.id);
            ps.execute();
            
            getCache().put(item.id, item);
            getDataCache().put(item.name, item.value);
        }
        catch(Exception e) {
            logger.error("failed to update data item", e);
            throw new DataException("failed to update data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public DataItem getDataItemFromDB(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                DataItem item = new DataItem();
                item.id = id;
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                return item;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data item", e);
            throw new DataException("failed to get data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public DataItem getDataItemFromDBByName(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data where name = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if(rs.next()) {
                DataItem item = new DataItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                return item;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data item", e);
            throw new DataException("failed to get data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public DataItem getDataItem(int id) {
        DataItem item = getCache().get(id);
        if(item != null) {
            return item;
        }
        
        item = getDataItemFromDB(id);
        if(item != null) {
            getCache().put(item.id, item);
            getDataCache().put(item.name, item.value);
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<DataItem> getDataItemsFromDB() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<DataItem> items = new ArrayList<DataItem>();
            while(rs.next()) {
                DataItem item = new DataItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                items.add(item);
            }
            return items;
        }
        catch(Exception e) {
            logger.error("failed to get data items", e);
            throw new DataException("failed to get data items");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public List<DataItem> getDataItems() {
        List<DataItem> result = new ArrayList<DataItem>();
        for(DataItem item : getCache().values()) {
            result.add(item);
        }
        return result;
    }
    
    public void deleteDataItem(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            
            DataItem item = this.getCache().get(id);
            getCache().remove(id);
            getDataCache().remove(item.name);
        }
        catch(Exception e) {
            logger.error("failed to delete data item", e);
            throw new DataException("failed to delete data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public int getValueAsInt(String name) {
        String value = this.getValue(name);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        }
        catch(Exception e) {
        }
        return ret;
    }
    
    public String getValue(String name) {
        if(name == null) {
            return null;
        }
        String value = this.getDataCache().get(name);
        if(value != null) {
            return value;
        }
        else {
            DataItem item = this.getDataItemFromDBByName(name);
            if(item == null) {
                return null;
            }
            else {
                this.getCache().put(item.id, item);
                this.getDataCache().put(item.name, item.value);
                return item.value;
            }
        }
    }
    
    public void setValue(String name, int value) {
        this.setValue(name, String.valueOf(value));
    }
    
    public void setValue(String name, String value) {
        DataItem item = this.getDataItemFromDBByName(name);
        if(item == null) {
            item = new DataItem();
            item.name = name;
            item.value = value;
            this.createDataItem(item);
        }
        else {
            item.value = value;
            this.updateDataItem(item);
        }
        this.getCache().put(item.id, item);
        this.getDataCache().put(item.name, item.value);
    }
    
    @Override
    public String getCacheProviderName() {
        return NAME;
    }

    @Override
    public boolean canPreload() {
        return true;
    }

    @Override
    public void reloadCache() {
        List<DataItem> items = getDataItemsFromDB();
        cache = new HashMap<Integer, DataItem>();
        dataCache = new HashMap<String, String>();
        for(DataItem item : items) {
            cache.put(item.id, item);
            dataCache.put(item.name, item.value);
        }
    }
}
