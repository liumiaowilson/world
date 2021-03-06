package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.DataSizeInfoDAO;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.DBCleanJob;
import org.wilson.world.item.DBCleaner;
import org.wilson.world.item.DataSizeInfo;
import org.wilson.world.item.DataSizeItem;
import org.wilson.world.item.DataSizeReportInfo;
import org.wilson.world.item.DataSizeTrackJob;
import org.wilson.world.item.ItemInfo;
import org.wilson.world.item.ItemTableInfo;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.item.PurgeDSInfoJob;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;

public class ItemManager {
    private static final Logger logger = Logger.getLogger(ItemManager.class);
    
    private static ItemManager instance;
    
    private List<ItemTypeProvider> providers = new ArrayList<ItemTypeProvider>();
    
    private DataSizeInfoDAO dao = null;
    
    private List<DBCleaner> cleaners = new ArrayList<DBCleaner>();
    
    private ItemManager() {
        this.dao = (DataSizeInfoDAO) DAOManager.getInstance().getDAO(DataSizeInfo.class);
        
        ScheduleManager.getInstance().addJob(new DataSizeTrackJob());
        ScheduleManager.getInstance().addJob(new PurgeDSInfoJob());
        ScheduleManager.getInstance().addJob(new DBCleanJob());
    }
    
    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }
    
    public void addDBCleaner(DBCleaner cleaner) {
        if(cleaner != null) {
            this.cleaners.add(cleaner);
        }
    }
    
    public void removeDBCleaner(DBCleaner cleaner) {
        if(cleaner != null) {
            this.cleaners.remove(cleaner);
        }
    }
    
    public void cleanDB() {
        for(DBCleaner cleaner : this.cleaners) {
            try {
                cleaner.clean();
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
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
    
    /**
     * This method may take a long time to run
     */
    @SuppressWarnings("rawtypes")
    public void trackDataSize() {
        for(ItemTypeProvider provider : this.providers) {
            String name = provider.getItemTypeName();
            DAO dao = provider.getDAO();
            int size = dao.getAll().size();
            
            DataSizeInfo info = new DataSizeInfo();
            info.name = name;
            info.size = size;
            info.time = System.currentTimeMillis();
            
            this.dao.create(info);
        }
    }
    
    public List<DataSizeItem> getDataSizeTrend(String name, TimeZone tz) {
        List<DataSizeItem> ret = new ArrayList<DataSizeItem>();
        
        if(StringUtils.isBlank(name)) {
            return ret;
        }
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        List<DataSizeInfo> infos = this.dao.getAllByName(name);
        for(DataSizeInfo info : infos) {
            DataSizeItem item = new DataSizeItem();
            item.timeStr = TimeUtils.getDateTimeUTCString(info.time, tz);
            item.size = info.size;
            ret.add(item);
        }
        
        return ret;
    }
    
    public List<String> getItemTypeNames() {
        List<String> ret = new ArrayList<String>();
        
        for(ItemTypeProvider provider : this.providers) {
            ret.add(provider.getItemTypeName());
        }
        
        return ret;
    }
    
    public List<DataSizeReportInfo> getDataSizeReport() {
        Map<String, DataSizeReportInfo> ret = new HashMap<String, DataSizeReportInfo>();
        List<DataSizeInfo> infos = this.dao.getAll();
        for(DataSizeInfo info : infos) {
            DataSizeReportInfo report = ret.get(info.name);
            if(report == null) {
                report = new DataSizeReportInfo();
                report.name = info.name;
                ret.put(report.name, report);
            }
            
            if(report.startTime == 0) {
                report.startTime = info.time;
                report.startCount = info.size;
            }
            
            report.endTime = info.time;
            report.endCount = info.size;
        }
        
        for(DataSizeReportInfo info : ret.values()) {
            if(info.startTime == info.endTime) {
                continue;
            }
            
            double ratio = FormatUtils.getRoundedValue((info.endCount - info.startCount) * 1.0 * TimeUtils.DAY_DURATION / (info.endTime - info.startTime));
            info.ratio = ratio;
        }
        
        List<DataSizeReportInfo> all = new ArrayList<DataSizeReportInfo>(ret.values());
        Collections.sort(all, new Comparator<DataSizeReportInfo>(){

            @Override
            public int compare(DataSizeReportInfo o1, DataSizeReportInfo o2) {
                if(o1.ratio > o2.ratio) {
                    return -1;
                }
                else if(o1.ratio < o2.ratio) {
                    return 1;
                }
                else {
                    return o1.name.compareTo(o2.name);
                }
            }
            
        });
        
        return all;
    }
    
    public ItemTypeProvider getItemTypeProvider(String itemType) {
        if(StringUtils.isBlank(itemType)) {
            return null;
        }
        
        for(ItemTypeProvider provider : this.providers) {
            if(itemType.equals(provider.getItemTypeName())) {
                return provider;
            }
        }
        return null;
    }
    
    public boolean isSupportedItemType(String itemType) {
        return this.getItemTypeProvider(itemType) != null;
    }
    
    @SuppressWarnings("rawtypes")
    public List<ItemInfo> getItemInfos(String itemType) {
        if(StringUtils.isBlank(itemType)) {
            return Collections.emptyList();
        }
        
        ItemTypeProvider provider = this.getItemTypeProvider(itemType);
        if(provider == null) {
            return Collections.emptyList();
        }
        
        DAO dao = provider.getDAO();
        List all = dao.getAll();
        List<ItemInfo> ret = new ArrayList<ItemInfo>();
        
        for(Object target : all) {
            try {
                ItemInfo info = new ItemInfo();
                info.id = Integer.parseInt(provider.getID(target));
                info.name = provider.getIdentifier(target);
                ret.add(info);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
        
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    public Object getItem(String itemType, String id) {
        if(StringUtils.isBlank(itemType) || StringUtils.isBlank(id)) {
            return null;
        }
        
        ItemTypeProvider provider = this.getItemTypeProvider(itemType);
        if(provider == null) {
            return null;
        }
        
        DAO dao = provider.getDAO();
        for(Object target : dao.getAll()) {
            if(id.equals(provider.getID(target))) {
                return target;
            }
        }
        
        return null;
    }
}
