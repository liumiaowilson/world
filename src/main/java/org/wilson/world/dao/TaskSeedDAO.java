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
import org.wilson.world.model.TaskSeed;

import com.mysql.jdbc.Statement;

public class TaskSeedDAO extends AbstractDAO<TaskSeed> {
    public static final String ITEM_TABLE_NAME = "task_seeds";
    
    private static final Logger logger = Logger.getLogger(TaskSeedDAO.class);

    @Override
    public void create(TaskSeed seed) {
        if(seed == null) {
            throw new DataException("task seed should not be null");
        }
        if(StringUtils.isBlank(seed.name)) {
            throw new DataException("task seed should have a valid name");
        }
        if(StringUtils.isBlank(seed.pattern)) {
            throw new DataException("task seed should have a valid pattern");
        }
        if(StringUtils.isBlank(seed.spawner)) {
            throw new DataException("task seed should have a valid spawner");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into task_seeds(name, pattern, spawner) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, seed.name);
            ps.setString(2, seed.pattern);
            ps.setString(3, seed.spawner);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                seed.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create task seed", e);
            throw new DataException("failed to create task seed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TaskSeed seed) {
        if(seed == null) {
            throw new DataException("task seed should not be null");
        }
        if(StringUtils.isBlank(seed.name)) {
            throw new DataException("task seed should have a valid name");
        }
        if(StringUtils.isBlank(seed.pattern)) {
            throw new DataException("task seed should have a valid pattern");
        }
        if(StringUtils.isBlank(seed.spawner)) {
            throw new DataException("task seed should have a valid spawner");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update task_seeds set name = ?, pattern = ?, spawner = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, seed.name);
            ps.setString(2, seed.pattern);
            ps.setString(3, seed.spawner);
            ps.setInt(4, seed.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update task seed", e);
            throw new DataException("failed to update task seed");
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
            String sql = "delete from task_seeds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete task seed", e);
            throw new DataException("failed to delete task seed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TaskSeed get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_seeds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TaskSeed seed = new TaskSeed();
                seed.id = id;
                seed.name = rs.getString(2);
                seed.pattern = rs.getString(3);
                seed.spawner = rs.getString(4);
                return seed;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get task seed", e);
            throw new DataException("failed to get task seed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskSeed> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_seeds;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TaskSeed> seeds = new ArrayList<TaskSeed>();
            while(rs.next()) {
                TaskSeed seed = new TaskSeed();
                seed.id = rs.getInt(1);
                seed.name = rs.getString(2);
                seed.pattern = rs.getString(3);
                seed.spawner = rs.getString(4);
                seeds.add(seed);
            }
            return seeds;
        }
        catch(Exception e) {
            logger.error("failed to get task seeds", e);
            throw new DataException("failed to get task seeds");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskSeed> query(QueryTemplate<TaskSeed> template, Object... args) {
        return new ArrayList<TaskSeed>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TaskSeed seed) {
        return seed.id;
    }

    @Override
    public StringBuffer exportSingle(TaskSeed t) {
        StringBuffer sb = new StringBuffer("INSERT INTO task_seeds (id, name, pattern, spawner) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(t.name);
        sb.append("','");
        sb.append(t.pattern);
        sb.append("','");
        sb.append(t.spawner);
        sb.append("');");
        return sb;
    }

}
