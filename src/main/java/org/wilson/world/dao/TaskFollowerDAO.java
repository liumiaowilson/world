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
import org.wilson.world.model.TaskFollower;

import com.mysql.jdbc.Statement;

public class TaskFollowerDAO extends AbstractDAO<TaskFollower> {
    public static final String ITEM_TABLE_NAME = "task_followers";
    
    private static final Logger logger = Logger.getLogger(TaskFollowerDAO.class);

    @Override
    public void create(TaskFollower follower) {
        if(follower == null) {
            throw new DataException("follower should not be null");
        }
        if(StringUtils.isBlank(follower.name)) {
            throw new DataException("follower should have a valid name");
        }
        if(StringUtils.isBlank(follower.symbol)) {
            throw new DataException("follower should have a valid symbol");
        }
        if(StringUtils.isBlank(follower.impl)) {
            throw new DataException("follower should have a valid impl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into task_followers(name, symbol, impl) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, follower.name);
            ps.setString(2, follower.symbol);
            ps.setString(3, follower.impl);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                follower.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create task follower", e);
            throw new DataException("failed to create task follower");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TaskFollower follower) {
        if(follower == null) {
            throw new DataException("follower should not be null");
        }
        if(StringUtils.isBlank(follower.name)) {
            throw new DataException("follower should have a valid name");
        }
        if(StringUtils.isBlank(follower.symbol)) {
            throw new DataException("follower should have a valid symbol");
        }
        if(StringUtils.isBlank(follower.impl)) {
            throw new DataException("follower should have a valid impl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update task_followers set name = ?, symbol = ?, impl = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, follower.name);
            ps.setString(2, follower.symbol);
            ps.setString(3, follower.impl);
            ps.setInt(4, follower.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update task follower", e);
            throw new DataException("failed to update task follower");
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
            String sql = "delete from task_followers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete task follower", e);
            throw new DataException("failed to delete task follower");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TaskFollower get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_followers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TaskFollower follower = new TaskFollower();
                follower.id = id;
                follower.name = rs.getString(2);
                follower.symbol = rs.getString(3);
                follower.impl = rs.getString(4);
                return follower;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get task follower", e);
            throw new DataException("failed to get task follower");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskFollower> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_followers;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TaskFollower> followers = new ArrayList<TaskFollower>();
            while(rs.next()) {
                TaskFollower follower = new TaskFollower();
                follower.id = rs.getInt(1);
                follower.name = rs.getString(2);
                follower.symbol = rs.getString(3);
                follower.impl = rs.getString(4);
                followers.add(follower);
            }
            return followers;
        }
        catch(Exception e) {
            logger.error("failed to get task followers", e);
            throw new DataException("failed to get task followers");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskFollower> query(QueryTemplate<TaskFollower> template, Object... args) {
        return new ArrayList<TaskFollower>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TaskFollower follower) {
        return follower.id;
    }

    @Override
    public StringBuffer exportSingle(TaskFollower t) {
        StringBuffer sb = new StringBuffer("INSERT INTO task_followers (id, name, symbol, impl) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.symbol));
        sb.append("','");
        sb.append(escape(t.impl));
        sb.append("');");
        return sb;
    }

}
