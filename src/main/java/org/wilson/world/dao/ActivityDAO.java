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
import org.wilson.world.model.Activity;

import com.mysql.jdbc.Statement;

public class ActivityDAO extends AbstractDAO<Activity> {
    public static final String ITEM_TABLE_NAME = "activities";
    
    private static final Logger logger = Logger.getLogger(ActivityDAO.class);

    @Override
    public void create(Activity activity) {
        if(activity == null) {
            throw new DataException("Activity should not be null");
        }
        if(StringUtils.isBlank(activity.name)) {
            throw new DataException("Activity should have a valid name");
        }
        if(StringUtils.isBlank(activity.content)) {
            throw new DataException("Activity should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into activities(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, activity.name);
            ps.setString(2, activity.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                activity.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create activity", e);
            throw new DataException("failed to create activity");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Activity activity) {
        if(activity == null) {
            throw new DataException("Activity should not be null");
        }
        if(StringUtils.isBlank(activity.name)) {
            throw new DataException("Activity should have a valid name");
        }
        if(StringUtils.isBlank(activity.content)) {
            throw new DataException("Activity should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update activities set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, activity.name);
            ps.setString(2, activity.content);
            ps.setInt(3, activity.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update activity", e);
            throw new DataException("failed to update activity");
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
            String sql = "delete from activities where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete activity", e);
            throw new DataException("failed to delete activity");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Activity get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from activities where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Activity activity = new Activity();
                activity.id = id;
                activity.name = rs.getString(2);
                activity.content = rs.getString(3);
                return activity;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get activity", e);
            throw new DataException("failed to get activity");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Activity> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from activities;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Activity> activities = new ArrayList<Activity>();
            while(rs.next()) {
            	Activity activity = new Activity();
                activity.id = rs.getInt(1);
                activity.name = rs.getString(2);
                activity.content = rs.getString(3);
                activities.add(activity);
            }
            return activities;
        }
        catch(Exception e) {
            logger.error("failed to get activities", e);
            throw new DataException("failed to get activities");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Activity> query(QueryTemplate<Activity> template, Object... args) {
        return new ArrayList<Activity>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Activity activity) {
        return activity.id;
    }

    @Override
    public StringBuffer exportSingle(Activity t) {
        StringBuffer sb = new StringBuffer("INSERT INTO activities (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
