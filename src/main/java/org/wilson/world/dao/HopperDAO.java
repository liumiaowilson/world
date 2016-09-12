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
import org.wilson.world.model.Hopper;

import com.mysql.jdbc.Statement;

public class HopperDAO extends AbstractDAO<Hopper> {
    public static final String ITEM_TABLE_NAME = "hoppers";
    
    private static final Logger logger = Logger.getLogger(HopperDAO.class);

    @Override
    public void create(Hopper hopper) {
        if(hopper == null) {
            throw new DataException("hopper should not be null");
        }
        if(StringUtils.isBlank(hopper.name)) {
            throw new DataException("hopper should have a valid name");
        }
        if(StringUtils.isBlank(hopper.description)) {
            throw new DataException("hopper should have a valid description");
        }
        if(StringUtils.isBlank(hopper.action)) {
            throw new DataException("hopper should have a valid action");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into hoppers(name, description, period, action) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hopper.name);
            ps.setString(2, hopper.description);
            ps.setInt(3, hopper.period);
            ps.setString(4, hopper.action);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                hopper.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create hopper", e);
            throw new DataException("failed to create hopper");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Hopper hopper) {
        if(hopper == null) {
            throw new DataException("hopper should not be null");
        }
        if(StringUtils.isBlank(hopper.name)) {
            throw new DataException("hopper should have a valid name");
        }
        if(StringUtils.isBlank(hopper.description)) {
            throw new DataException("hopper should have a valid description");
        }
        if(StringUtils.isBlank(hopper.action)) {
            throw new DataException("hopper should have a valid action");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update hoppers set name = ?, description = ?, period = ?, action = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, hopper.name);
            ps.setString(2, hopper.description);
            ps.setInt(3, hopper.period);
            ps.setString(4, hopper.action);
            ps.setInt(5, hopper.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update hopper", e);
            throw new DataException("failed to update hopper");
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
            String sql = "delete from hoppers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete hopper", e);
            throw new DataException("failed to delete hopper");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Hopper get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hoppers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Hopper hopper = new Hopper();
                hopper.id = id;
                hopper.name = rs.getString(2);
                hopper.description = rs.getString(3);
                hopper.period = rs.getInt(4);
                hopper.action = rs.getString(5);
                return hopper;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get hopper", e);
            throw new DataException("failed to get hopper");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Hopper> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hoppers;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Hopper> hoppers = new ArrayList<Hopper>();
            while(rs.next()) {
                Hopper hopper = new Hopper();
                hopper.id = rs.getInt(1);
                hopper.name = rs.getString(2);
                hopper.description = rs.getString(3);
                hopper.period = rs.getInt(4);
                hopper.action = rs.getString(5);
                hoppers.add(hopper);
            }
            return hoppers;
        }
        catch(Exception e) {
            logger.error("failed to get hoppers", e);
            throw new DataException("failed to get hoppers");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Hopper> query(QueryTemplate<Hopper> template, Object... args) {
        return new ArrayList<Hopper>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Hopper hopper) {
        return hopper.id;
    }

    @Override
    public StringBuffer exportSingle(Hopper t) {
        StringBuffer sb = new StringBuffer("INSERT INTO hoppers (id, name, description, period, action) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(t.period);
        sb.append(",");
        sb.append(escapeStr(t.action));
        sb.append(");");
        return sb;
    }

}
