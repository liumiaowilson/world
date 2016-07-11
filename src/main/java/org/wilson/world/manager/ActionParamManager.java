package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.ActionParam;

import com.mysql.jdbc.Statement;

public class ActionParamManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "action_param";
    
    public static final String ITEM_TABLE_NAME = "action_params";
    
    private static final Logger logger = Logger.getLogger(ActionParamManager.class);
    
    private static ActionParamManager instance;
    
    private Map<Integer, ActionParam> cache = null;
    
    private ActionParamManager() {
        ItemManager.getInstance().registerItemTypeProvider(this);
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static ActionParamManager getInstance() {
        if(instance == null) {
            instance = new ActionParamManager();
        }
        return instance;
    }
    
    private Map<Integer, ActionParam> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    public void createActionParam(ActionParam param) {
        if(param == null) {
            throw new DataException("action param should not be null");
        }
        if(StringUtils.isBlank(param.name)) {
            throw new DataException("action param should have a valid name");
        }
        if(StringUtils.isBlank(param.defaultValue)) {
            throw new DataException("action param should have a valid default value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into action_params(action_id, name, default_value) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, param.actionId);
            ps.setString(2, param.name);
            ps.setString(3, param.defaultValue);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                param.id = id;
                this.getCache().put(param.id, param);
            }
        }
        catch(Exception e) {
            logger.error("failed to create action param", e);
            throw new DataException("failed to create action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public ActionParam getActionParamFromDB(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from action_params where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ActionParam param = new ActionParam();
                param.id = id;
                param.actionId = rs.getInt(2);
                param.name = rs.getString(3);
                param.defaultValue = rs.getString(4);
                return param;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get action param", e);
            throw new DataException("failed to get action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public ActionParam getActionParam(int id) {
        ActionParam param = getCache().get(id);
        if(param != null) {
            return param;
        }
        
        param = getActionParamFromDB(id);
        if(param != null) {
            getCache().put(param.id, param);
            return param;
        }
        else {
            return null;
        }
    }
    
    public List<ActionParam> getActionParamsFromDB() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from action_params;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ActionParam> params = new ArrayList<ActionParam>();
            while(rs.next()) {
                ActionParam param = new ActionParam();
                param.id = rs.getInt(1);
                param.actionId = rs.getInt(2);
                param.name = rs.getString(3);
                param.defaultValue = rs.getString(4);
                params.add(param);
            }
            return params;
        }
        catch(Exception e) {
            logger.error("failed to get action params", e);
            throw new DataException("failed to get action params");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public List<ActionParam> getActionParams() {
        List<ActionParam> result = new ArrayList<ActionParam>();
        for(ActionParam param : getCache().values()) {
            result.add(param);
        }
        return result;
    }
    
    public void updateActionParam(ActionParam param) {
        if(param == null) {
            throw new DataException("action param should not be null");
        }
        if(StringUtils.isBlank(param.name)) {
            throw new DataException("action param should have a valid name");
        }
        if(StringUtils.isBlank(param.defaultValue)) {
            throw new DataException("action param should have a valid default value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update action_params set action_id = ?, name = ?, default_value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, param.actionId);
            ps.setString(2, param.name);
            ps.setString(3, param.defaultValue);
            ps.setInt(4, param.id);
            ps.execute();
            
            getCache().put(param.id, param);
        }
        catch(Exception e) {
            logger.error("failed to update action param", e);
            throw new DataException("failed to update action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public void deleteActionParam(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from action_params where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            
            getCache().remove(id);
        }
        catch(Exception e) {
            logger.error("failed to delete action param", e);
            throw new DataException("failed to delete action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public List<ActionParam> getActionParamsByActionId(int actionId) {
        List<ActionParam> ret = new ArrayList<ActionParam>();
        for(ActionParam param : this.getCache().values()) {
            if(param.actionId == actionId) {
                ret.add(param);
            }
        }
        Collections.sort(ret, new Comparator<ActionParam>(){
            @Override
            public int compare(ActionParam o1, ActionParam o2) {
                return o1.id - o2.id;
            }
        });
        return ret;
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof ActionParam;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        ActionParam param = (ActionParam)target;
        return String.valueOf(param.id);
    }

    @Override
    public String getCacheProviderName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public void reloadCache() {
        List<ActionParam> params = getActionParamsFromDB();
        cache = new HashMap<Integer, ActionParam>();
        for(ActionParam param : params) {
            cache.put(param.id, param);
        }
    }

    @Override
    public boolean canPreload() {
        return true;
    }
}
