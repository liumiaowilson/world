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
import org.wilson.world.model.TaskTag;

import com.mysql.jdbc.Statement;

public class TaskTagDAO extends AbstractDAO<TaskTag> {
    public static final String ITEM_TABLE_NAME = "task_tags";
    
    private static final Logger logger = Logger.getLogger(TaskTagDAO.class);

    @Override
    public void create(TaskTag tag) {
        if(tag == null) {
            throw new DataException("tag should not be null");
        }
        if(StringUtils.isBlank(tag.tags)) {
            throw new DataException("tag should have a valid tags");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into task_tags(task_id, tags) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tag.taskId);
            ps.setString(2, tag.tags);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                tag.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create tag", e);
            throw new DataException("failed to create tag");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TaskTag tag) {
        if(tag == null) {
            throw new DataException("tag should not be null");
        }
        if(StringUtils.isBlank(tag.tags)) {
            throw new DataException("tag should have a valid tags");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update task_tags set task_id = ?, tags = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, tag.taskId);
            ps.setString(2, tag.tags);
            ps.setInt(3, tag.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update tag", e);
            throw new DataException("failed to update tag");
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
            String sql = "delete from task_tags where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete tag", e);
            throw new DataException("failed to delete tag");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TaskTag get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_tags where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TaskTag tag = new TaskTag();
                tag.id = id;
                tag.taskId = rs.getInt(2);
                tag.tags = rs.getString(3);
                return tag;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get tag", e);
            throw new DataException("failed to get tag");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskTag> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_tags;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TaskTag> tags = new ArrayList<TaskTag>();
            while(rs.next()) {
                TaskTag tag = new TaskTag();
                tag.id = rs.getInt(1);
                tag.taskId = rs.getInt(2);
                tag.tags = rs.getString(3);
                tags.add(tag);
            }
            return tags;
        }
        catch(Exception e) {
            logger.error("failed to get tags", e);
            throw new DataException("failed to get tags");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskTag> query(QueryTemplate<TaskTag> template, Object... args) {
        return new ArrayList<TaskTag>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TaskTag tag) {
        return tag.id;
    }

    @Override
    public StringBuffer exportSingle(TaskTag t) {
        StringBuffer sb = new StringBuffer("INSERT INTO task_tags (id, task_id, tags) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.taskId);
        sb.append(",");
        sb.append(escapeStr(t.tags));
        sb.append(");");
        return sb;
    }

}
