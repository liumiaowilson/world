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
import org.wilson.world.model.RoleAttr;

import com.mysql.jdbc.Statement;

public class RoleAttrDAO extends AbstractDAO<RoleAttr> {
    public static final String ITEM_TABLE_NAME = "role_attrs";
    
    private static final Logger logger = Logger.getLogger(RoleAttrDAO.class);

    @Override
    public void create(RoleAttr attr) {
        if(attr == null) {
            throw new DataException("RoleAttr should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("RoleAttr should have a valid name");
        }
        if(StringUtils.isBlank(attr.description)) {
            throw new DataException("RoleAttr should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into role_attrs(name, description) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, attr.name);
            ps.setString(2, attr.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                attr.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create role attr", e);
            throw new DataException("failed to create role attr");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(RoleAttr attr) {
        if(attr == null) {
            throw new DataException("RoleAttr should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("RoleAttr should have a valid name");
        }
        if(StringUtils.isBlank(attr.description)) {
            throw new DataException("RoleAttr should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update role_attrs set name = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, attr.name);
            ps.setString(2, attr.description);
            ps.setInt(3, attr.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update role attr", e);
            throw new DataException("failed to update role attr");
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
            String sql = "delete from role_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete role attr", e);
            throw new DataException("failed to delete role attr");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public RoleAttr get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from role_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                RoleAttr attr = new RoleAttr();
                attr.id = id;
                attr.name = rs.getString(2);
                attr.description = rs.getString(3);
                return attr;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get role attr", e);
            throw new DataException("failed to get role attr");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RoleAttr> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from role_attrs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<RoleAttr> attrs = new ArrayList<RoleAttr>();
            while(rs.next()) {
                RoleAttr attr = new RoleAttr();
                attr.id = rs.getInt(1);
                attr.name = rs.getString(2);
                attr.description = rs.getString(3);
                attrs.add(attr);
            }
            return attrs;
        }
        catch(Exception e) {
            logger.error("failed to get role attrs", e);
            throw new DataException("failed to get role attrs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RoleAttr> query(QueryTemplate<RoleAttr> template, Object... args) {
        return new ArrayList<RoleAttr>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(RoleAttr attr) {
        return attr.id;
    }

    @Override
    public StringBuffer exportSingle(RoleAttr t) {
        StringBuffer sb = new StringBuffer("INSERT INTO role_attrs (id, name, description) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(");");
        return sb;
    }

}
