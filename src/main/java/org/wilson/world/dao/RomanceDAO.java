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
import org.wilson.world.model.Romance;

import com.mysql.jdbc.Statement;

public class RomanceDAO extends AbstractDAO<Romance> {
    public static final String ITEM_TABLE_NAME = "romances";
    
    private static final Logger logger = Logger.getLogger(RomanceDAO.class);

    @Override
    public void create(Romance romance) {
        if(romance == null) {
            throw new DataException("romance should not be null");
        }
        if(StringUtils.isBlank(romance.name)) {
            throw new DataException("romance should have a valid name");
        }
        if(StringUtils.isBlank(romance.content)) {
            throw new DataException("romance should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into romances(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, romance.name);
            ps.setString(2, romance.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                romance.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create romance", e);
            throw new DataException("failed to create romance");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Romance romance) {
        if(romance == null) {
            throw new DataException("romance should not be null");
        }
        if(StringUtils.isBlank(romance.name)) {
            throw new DataException("romance should have a valid name");
        }
        if(StringUtils.isBlank(romance.content)) {
            throw new DataException("romance should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update romances set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, romance.name);
            ps.setString(2, romance.content);
            ps.setInt(3, romance.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update romance", e);
            throw new DataException("failed to update romance");
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
            String sql = "delete from romances where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete romance", e);
            throw new DataException("failed to delete romance");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Romance get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from romances where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Romance romance = new Romance();
                romance.id = id;
                romance.name = rs.getString(2);
                romance.content = rs.getString(3);
                return romance;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get romance", e);
            throw new DataException("failed to get romance");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Romance> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from romances;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Romance> romances = new ArrayList<Romance>();
            while(rs.next()) {
                Romance romance = new Romance();
                romance.id = rs.getInt(1);
                romance.name = rs.getString(2);
                romance.content = rs.getString(3);
                romances.add(romance);
            }
            return romances;
        }
        catch(Exception e) {
            logger.error("failed to get romances", e);
            throw new DataException("failed to get romances");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Romance> query(QueryTemplate<Romance> template, Object... args) {
        return new ArrayList<Romance>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Romance romance) {
        return romance.id;
    }

    @Override
    public StringBuffer exportSingle(Romance t) {
        StringBuffer sb = new StringBuffer("INSERT INTO romances (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
