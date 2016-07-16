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
import org.wilson.world.model.TaskAttr;

import com.mysql.jdbc.Statement;

public class TaskAttrDAO extends AbstractDAO<TaskAttr> {
    public static final String ITEM_TABLE_NAME = "task_attrs";
    
    private static final Logger logger = Logger.getLogger(TaskAttrDAO.class);
    
    @Override
    public void create(TaskAttr attr) {
        if(attr == null) {
            throw new DataException("task attribute should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("task attribute should have a valid name");
        }
        if(StringUtils.isBlank(attr.value)) {
            throw new DataException("task attribute should have a valid value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into task_attrs(task_id, name, value) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, attr.taskId);
            ps.setString(2, attr.name);
            ps.setString(3, attr.value);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                attr.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create task attribute", e);
            throw new DataException("failed to create task attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TaskAttr attr) {
        if(attr == null) {
            throw new DataException("task attribute should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("task attribute should have a valid name");
        }
        if(StringUtils.isBlank(attr.value)) {
            throw new DataException("task attribute should have a valid value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update task_attrs set task_id = ?, name = ?, value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, attr.taskId);
            ps.setString(2, attr.name);
            ps.setString(3, attr.value);
            ps.setInt(4, attr.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update task attribute", e);
            throw new DataException("failed to update task attribute");
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
            String sql = "delete from task_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete task attribute", e);
            throw new DataException("failed to delete task attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TaskAttr get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TaskAttr attr = new TaskAttr();
                attr.id = id;
                attr.taskId = rs.getInt(2);
                attr.name = rs.getString(3);
                attr.value = rs.getString(4);
                return attr;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get task attribute", e);
            throw new DataException("failed to get task attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskAttr> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_attrs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TaskAttr> attrs = new ArrayList<TaskAttr>();
            while(rs.next()) {
                TaskAttr attr = new TaskAttr();
                attr.id = rs.getInt(1);
                attr.taskId = rs.getInt(2);
                attr.name = rs.getString(3);
                attr.value = rs.getString(4);
                attrs.add(attr);
            }
            return attrs;
        }
        catch(Exception e) {
            logger.error("failed to get task attrs", e);
            throw new DataException("failed to get task attrs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskAttr> query(QueryTemplate<TaskAttr> template, Object... args) {
        return new ArrayList<TaskAttr>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TaskAttr attr) {
        return attr.id;
    }

}
