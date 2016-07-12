package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.ActionParam;

import com.mysql.jdbc.Statement;

public class ActionParamDAO extends AbstractDAO<ActionParam> {
    public static final String ITEM_TABLE_NAME = "action_params";
    
    private static final Logger logger = Logger.getLogger(ActionParamDAO.class);
    
    @Override
    public void create(ActionParam param) {
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

    @Override
    public void update(ActionParam param) {
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
        }
        catch(Exception e) {
            logger.error("failed to update action param", e);
            throw new DataException("failed to update action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public void delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from action_params where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete action param", e);
            throw new DataException("failed to delete action param");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ActionParam get(int id) {
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

    @Override
    public List<ActionParam> getAll() {
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

    @Override
    public List<ActionParam> query(QueryTemplate template, Object... args) {
        return new ArrayList<ActionParam>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

}
