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
import org.wilson.world.model.Task;

import com.mysql.jdbc.Statement;

public class TaskDAO extends AbstractDAO<Task> {
    public static final String ITEM_TABLE_NAME = "tasks";
    
    private static final Logger logger = Logger.getLogger(TaskDAO.class);

    @Override
    public void create(Task task) {
        if(task == null) {
            throw new DataException("task should not be null");
        }
        if(StringUtils.isBlank(task.name)) {
            throw new DataException("task should have a valid name");
        }
        if(StringUtils.isBlank(task.content)) {
            throw new DataException("task should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into tasks(name, content, created_time, modified_time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.name);
            ps.setString(2, task.content);
            ps.setLong(3, task.createdTime);
            ps.setLong(4, task.modifiedTime);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                task.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create task", e);
            throw new DataException("failed to create task");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Task task) {
        if(task == null) {
            throw new DataException("task should not be null");
        }
        if(StringUtils.isBlank(task.name)) {
            throw new DataException("task should have a valid name");
        }
        if(StringUtils.isBlank(task.content)) {
            throw new DataException("task should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update tasks set name = ?, content = ?, modified_time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, task.name);
            ps.setString(2, task.content);
            ps.setLong(3, task.modifiedTime);
            ps.setInt(4, task.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update task", e);
            throw new DataException("failed to update task");
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
            String sql = "delete from tasks where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete task", e);
            throw new DataException("failed to delete task");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Task get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tasks where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Task task = new Task();
                task.id = id;
                task.name = rs.getString(2);
                task.content = rs.getString(3);
                task.createdTime = rs.getLong(4);
                task.modifiedTime = rs.getLong(5);
                return task;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get task", e);
            throw new DataException("failed to get task");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Task> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tasks;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Task> tasks = new ArrayList<Task>();
            while(rs.next()) {
                Task task = new Task();
                task.id = rs.getInt(1);
                task.name = rs.getString(2);
                task.content = rs.getString(3);
                task.createdTime = rs.getLong(4);
                task.modifiedTime = rs.getLong(5);
                tasks.add(task);
            }
            return tasks;
        }
        catch(Exception e) {
            logger.error("failed to get tasks", e);
            throw new DataException("failed to get tasks");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Task> query(QueryTemplate<Task> template, Object... args) {
        return new ArrayList<Task>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Task task) {
        return task.id;
    }

    @Override
    public StringBuffer exportSingle(Task t) {
        StringBuffer sb = new StringBuffer("INSERT INTO tasks (id, name, content, created_time, modified_time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(t.createdTime);
        sb.append(",");
        sb.append(t.modifiedTime);
        sb.append(");");
        return sb;
    }

}
