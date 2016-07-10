package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Action;
import org.wilson.world.model.ActionParam;

import com.mysql.jdbc.Statement;

public class ActionManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "action";
    
    public static final String ITEM_TABLE_NAME = "actions";
    
    private static final Logger logger = Logger.getLogger(ActionManager.class);
    
    private static ActionManager instance;
    
    private Map<Integer, Action> cache = null;
    
    private ActionManager() {
        ItemManager.getInstance().registerItemTypeProvider(this);
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static ActionManager getInstance() {
        if(instance == null) {
            instance = new ActionManager();
        }
        return instance;
    }
    
    private Map<Integer, Action> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    public void createAction(Action action) {
        if(action == null) {
            throw new DataException("action should not be null");
        }
        if(StringUtils.isBlank(action.name)) {
            throw new DataException("action should have a valid name");
        }
        if(StringUtils.isBlank(action.script)) {
            throw new DataException("action should have a valid script");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into actions(name, script) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, action.name);
            ps.setString(2, action.script);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                action.id = id;
                this.getCache().put(action.id, action);
            }
            
            for(ActionParam param : action.params) {
                param.actionId = action.id;
                ActionParamManager.getInstance().createActionParam(param);
            }
        }
        catch(Exception e) {
            logger.error("failed to create action", e);
            throw new DataException("failed to create action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public Action getActionFromDB(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from actions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Action action = new Action();
                action.id = id;
                action.name = rs.getString(2);
                action.script = rs.getString(3);
                return action;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get action", e);
            throw new DataException("failed to get action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public Action getAction(int id) {
        Action action = getCache().get(id);
        if(action != null) {
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            return action;
        }
        
        action = getActionFromDB(id);
        if(action != null) {
            getCache().put(action.id, action);
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            return action;
        }
        else {
            return null;
        }
    }
    
    public List<Action> getActionsFromDB() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from actions;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Action> actions = new ArrayList<Action>();
            while(rs.next()) {
                Action action = new Action();
                action.id = rs.getInt(1);
                action.name = rs.getString(2);
                action.script = rs.getString(3);
                actions.add(action);
            }
            return actions;
        }
        catch(Exception e) {
            logger.error("failed to get actions", e);
            throw new DataException("failed to get actions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public List<Action> getActions() {
        List<Action> result = new ArrayList<Action>();
        for(Action action : getCache().values()) {
            action.params = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            result.add(action);
        }
        return result;
    }
    
    private boolean hasActionParam(List<ActionParam> params, ActionParam param) {
        for(ActionParam p : params) {
            if(p.id == param.id) {
                return true;
            }
        }
        return false;
    }
    
    public void updateAction(Action action) {
        if(action == null) {
            throw new DataException("action should not be null");
        }
        if(StringUtils.isBlank(action.name)) {
            throw new DataException("action should have a valid name");
        }
        if(StringUtils.isBlank(action.script)) {
            throw new DataException("action should have a valid script");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update actions set name = ?, script = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, action.name);
            ps.setString(2, action.script);
            ps.setInt(3, action.id);
            ps.execute();
            
            List<ActionParam> oldParams = ActionParamManager.getInstance().getActionParamsByActionId(action.id);
            List<ActionParam> create = new ArrayList<ActionParam>();
            List<ActionParam> update = new ArrayList<ActionParam>();
            List<ActionParam> delete = new ArrayList<ActionParam>();
            for(ActionParam p : action.params) {
                if(p.id == 0) {
                    create.add(p);
                }
                else if(hasActionParam(oldParams, p)) {
                    update.add(p);
                }
                else {
                    delete.add(p);
                }
            }
            
            for(ActionParam param : create) {
                param.actionId = action.id;
                ActionParamManager.getInstance().createActionParam(param);
            }
            
            for(ActionParam param : update) {
                param.actionId = action.id;
                ActionParamManager.getInstance().updateActionParam(param);
            }
            
            for(ActionParam param : delete) {
                ActionParamManager.getInstance().deleteActionParam(param.id);
            }
            
            getCache().put(action.id, action);
        }
        catch(Exception e) {
            logger.error("failed to update action", e);
            throw new DataException("failed to update action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public void deleteAction(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        
        Action oldAction = this.getAction(id);
        try {
            con = DBUtils.getConnection();
            String sql = "delete from actions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            
            for(ActionParam param : oldAction.params) {
                ActionParamManager.getInstance().deleteActionParam(param.id);
            }
            
            getCache().remove(id);
        }
        catch(Exception e) {
            logger.error("failed to delete action", e);
            throw new DataException("failed to delete action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
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
        return target instanceof Action;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Action action = (Action)target;
        return String.valueOf(action.id);
    }

    @Override
    public String getCacheProviderName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public void reloadCache() {
        List<Action> actions = getActionsFromDB();
        cache = new HashMap<Integer, Action>();
        for(Action action : actions) {
            cache.put(action.id, action);
        }
    }

    @Override
    public boolean canPreload() {
        return true;
    }
}
