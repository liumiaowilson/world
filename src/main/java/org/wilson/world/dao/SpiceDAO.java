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
import org.wilson.world.model.Spice;

import com.mysql.jdbc.Statement;

public class SpiceDAO extends AbstractDAO<Spice> {
    public static final String ITEM_TABLE_NAME = "spices";
    
    private static final Logger logger = Logger.getLogger(SpiceDAO.class);

    @Override
    public void create(Spice spice) {
        if(spice == null) {
            throw new DataException("Spice should not be null");
        }
        if(StringUtils.isBlank(spice.name)) {
            throw new DataException("Spice should have a valid name");
        }
        if(StringUtils.isBlank(spice.prerequisite)) {
            throw new DataException("Spice should have a valid prerequisite");
        }
        if(StringUtils.isBlank(spice.content)) {
            throw new DataException("Spice should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into spices(name, prerequisite, cost, content) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, spice.name);
            ps.setString(2, spice.prerequisite);
            ps.setInt(3, spice.cost);
            ps.setString(4, spice.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                spice.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create spice", e);
            throw new DataException("failed to create spice");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Spice spice) {
        if(spice == null) {
            throw new DataException("Spice should not be null");
        }
        if(StringUtils.isBlank(spice.name)) {
            throw new DataException("Spice should have a valid name");
        }
        if(StringUtils.isBlank(spice.prerequisite)) {
            throw new DataException("Spice should have a valid prerequisite");
        }
        if(StringUtils.isBlank(spice.content)) {
            throw new DataException("Spice should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update spices set name = ?, prerequisite = ?, cost = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, spice.name);
            ps.setString(2, spice.prerequisite);
            ps.setInt(3, spice.cost);
            ps.setString(4, spice.content);
            ps.setInt(5, spice.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update spice", e);
            throw new DataException("failed to update spice");
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
            String sql = "delete from spices where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete spice", e);
            throw new DataException("failed to delete spice");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Spice get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from spices where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Spice spice = new Spice();
                spice.id = id;
                spice.name = rs.getString(2);
                spice.prerequisite = rs.getString(3);
                spice.cost = rs.getInt(4);
                spice.content = rs.getString(5);
                return spice;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get spice", e);
            throw new DataException("failed to get spice");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Spice> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from spices;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Spice> spices = new ArrayList<Spice>();
            while(rs.next()) {
                Spice spice = new Spice();
                spice.id = rs.getInt(1);
                spice.name = rs.getString(2);
                spice.prerequisite = rs.getString(3);
                spice.cost = rs.getInt(4);
                spice.content = rs.getString(5);
                spices.add(spice);
            }
            return spices;
        }
        catch(Exception e) {
            logger.error("failed to get spices", e);
            throw new DataException("failed to get spices");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Spice> query(QueryTemplate<Spice> template, Object... args) {
        return new ArrayList<Spice>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Spice spice) {
        return spice.id;
    }

    @Override
    public StringBuffer exportSingle(Spice t) {
        StringBuffer sb = new StringBuffer("INSERT INTO spices (id, name, prerequisite, cost, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.prerequisite));
        sb.append(",");
        sb.append(t.cost);
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
