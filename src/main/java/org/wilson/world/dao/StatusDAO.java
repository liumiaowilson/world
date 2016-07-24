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
import org.wilson.world.model.Status;

import com.mysql.jdbc.Statement;

public class StatusDAO extends AbstractDAO<Status> {
    public static final String ITEM_TABLE_NAME = "statuses";
    
    private static final Logger logger = Logger.getLogger(StatusDAO.class);

    @Override
    public void create(Status status) {
        if(status == null) {
            throw new DataException("status should not be null");
        }
        if(StringUtils.isBlank(status.name)) {
            throw new DataException("status should have a valid name");
        }
        if(StringUtils.isBlank(status.icon)) {
            throw new DataException("status should have a valid icon");
        }
        if(StringUtils.isBlank(status.description)) {
            throw new DataException("status should have a valid description");
        }
        if(StringUtils.isBlank(status.activator)) {
            throw new DataException("status should have a valid activator");
        }
        if(StringUtils.isBlank(status.deactivator)) {
            throw new DataException("status should have a valid deactivator");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into statuses(name, icon, description, activator, deactivator) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, status.name);
            ps.setString(2, status.icon);
            ps.setString(3, status.description);
            ps.setString(4, status.activator);
            ps.setString(5, status.deactivator);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                status.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create status", e);
            throw new DataException("failed to create status");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Status status) {
        if(status == null) {
            throw new DataException("status should not be null");
        }
        if(StringUtils.isBlank(status.name)) {
            throw new DataException("status should have a valid name");
        }
        if(StringUtils.isBlank(status.icon)) {
            throw new DataException("status should have a valid icon");
        }
        if(StringUtils.isBlank(status.description)) {
            throw new DataException("status should have a valid description");
        }
        if(StringUtils.isBlank(status.activator)) {
            throw new DataException("status should have a valid activator");
        }
        if(StringUtils.isBlank(status.deactivator)) {
            throw new DataException("status should have a valid deactivator");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update statuses set name = ?, icon = ?, description = ?, activator = ?, deactivator = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, status.name);
            ps.setString(2, status.icon);
            ps.setString(3, status.description);
            ps.setString(4, status.activator);
            ps.setString(5, status.deactivator);
            ps.setInt(6, status.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update status", e);
            throw new DataException("failed to update status");
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
            String sql = "delete from statuses where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete status", e);
            throw new DataException("failed to delete status");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Status get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from statuses where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Status status = new Status();
                status.id = id;
                status.name = rs.getString(2);
                status.icon = rs.getString(3);
                status.description = rs.getString(4);
                status.activator = rs.getString(5);
                status.deactivator = rs.getString(6);
                return status;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get status", e);
            throw new DataException("failed to get status");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Status> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from statuses;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Status> statuses = new ArrayList<Status>();
            while(rs.next()) {
                Status status = new Status();
                status.id = rs.getInt(1);
                status.name = rs.getString(2);
                status.icon = rs.getString(3);
                status.description = rs.getString(4);
                status.activator = rs.getString(5);
                status.deactivator = rs.getString(6);
                statuses.add(status);
            }
            return statuses;
        }
        catch(Exception e) {
            logger.error("failed to get statuses", e);
            throw new DataException("failed to get statuses");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Status> query(QueryTemplate<Status> template, Object... args) {
        return new ArrayList<Status>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Status status) {
        return status.id;
    }

    @Override
    public StringBuffer exportSingle(Status t) {
        StringBuffer sb = new StringBuffer("INSERT INTO statuses (id, name, icon, description, activator, deactivator) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.icon));
        sb.append("','");
        sb.append(escape(t.description));
        sb.append("','");
        sb.append(escape(t.activator));
        sb.append("','");
        sb.append(escape(t.deactivator));
        sb.append("');");
        return sb;
    }

}
