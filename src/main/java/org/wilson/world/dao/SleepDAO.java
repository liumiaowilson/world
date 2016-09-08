package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Sleep;

import com.mysql.jdbc.Statement;

public class SleepDAO extends AbstractDAO<Sleep> {
    public static final String ITEM_TABLE_NAME = "sleeps";
    
    private static final Logger logger = Logger.getLogger(SleepDAO.class);

    @Override
    public void create(Sleep sleep) {
        if(sleep == null) {
            throw new DataException("Sleep should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into sleeps(start_time, end_time, quality, dreams) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sleep.startTime);
            ps.setLong(2, sleep.endTime);
            ps.setInt(3, sleep.quality);
            ps.setInt(4, sleep.dreams);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                sleep.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create sleep", e);
            throw new DataException("failed to create sleep");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Sleep sleep) {
        if(sleep == null) {
            throw new DataException("Sleep should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update sleeps set start_time = ?, end_time = ?, quality = ?, dreams = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setLong(1, sleep.startTime);
            ps.setLong(2, sleep.endTime);
            ps.setInt(3, sleep.quality);
            ps.setInt(4, sleep.dreams);
            ps.setInt(5, sleep.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update sleep", e);
            throw new DataException("failed to update sleep");
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
            String sql = "delete from sleeps where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete sleep", e);
            throw new DataException("failed to delete sleep");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Sleep get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from sleeps where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Sleep sleep = new Sleep();
                sleep.id = id;
                sleep.startTime = rs.getLong(2);
                sleep.endTime = rs.getLong(3);
                sleep.quality = rs.getInt(4);
                sleep.dreams = rs.getInt(4);
                return sleep;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get sleep", e);
            throw new DataException("failed to get sleep");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Sleep> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from sleeps;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Sleep> sleeps = new ArrayList<Sleep>();
            while(rs.next()) {
                Sleep sleep = new Sleep();
                sleep.id = rs.getInt(1);
                sleep.startTime = rs.getLong(2);
                sleep.endTime = rs.getLong(3);
                sleep.quality = rs.getInt(4);
                sleep.dreams = rs.getInt(5);
                sleeps.add(sleep);
            }
            return sleeps;
        }
        catch(Exception e) {
            logger.error("failed to get sleeps", e);
            throw new DataException("failed to get sleeps");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Sleep> query(QueryTemplate<Sleep> template, Object... args) {
        return new ArrayList<Sleep>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Sleep sleep) {
        return sleep.id;
    }

    @Override
    public StringBuffer exportSingle(Sleep t) {
        StringBuffer sb = new StringBuffer("INSERT INTO sleeps (id, start_time, end_time, quality, dreams) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.startTime);
        sb.append(",");
        sb.append(t.endTime);
        sb.append(",");
        sb.append(t.quality);
        sb.append(",");
        sb.append(t.dreams);
        sb.append(");");
        return sb;
    }

}
