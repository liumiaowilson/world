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
import org.wilson.world.model.Role;

import com.mysql.jdbc.Statement;

public class RoleDAO extends AbstractDAO<Role> {
    public static final String ITEM_TABLE_NAME = "roles";
    
    private static final Logger logger = Logger.getLogger(RoleDAO.class);

    @Override
    public void create(Role role) {
        if(role == null) {
            throw new DataException("Role should not be null");
        }
        if(StringUtils.isBlank(role.name)) {
            throw new DataException("Role should have a valid name");
        }
        if(StringUtils.isBlank(role.description)) {
            throw new DataException("Role should have a valid description");
        }
        if(StringUtils.isBlank(role.attrIds)) {
            throw new DataException("Role should have a valid attrIds");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into roles(name, description, attr_ids) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name);
            ps.setString(2, role.description);
            ps.setString(3, role.attrIds);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                role.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create role", e);
            throw new DataException("failed to create role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Role role) {
        if(role == null) {
            throw new DataException("Role should not be null");
        }
        if(StringUtils.isBlank(role.name)) {
            throw new DataException("Role should have a valid name");
        }
        if(StringUtils.isBlank(role.description)) {
            throw new DataException("Role should have a valid description");
        }
        if(StringUtils.isBlank(role.attrIds)) {
            throw new DataException("Role should have a valid attrIds");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update roles set name = ?, description = ?, attr_ids = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, role.name);
            ps.setString(2, role.description);
            ps.setString(3, role.attrIds);
            ps.setInt(4, role.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update role", e);
            throw new DataException("failed to update role");
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
            String sql = "delete from roles where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete role", e);
            throw new DataException("failed to delete role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Role get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from roles where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Role role = new Role();
                role.id = id;
                role.name = rs.getString(2);
                role.description = rs.getString(3);
                role.attrIds = rs.getString(4);
                return role;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get role", e);
            throw new DataException("failed to get role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Role> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from roles;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Role> roles = new ArrayList<Role>();
            while(rs.next()) {
                Role role = new Role();
                role.id = rs.getInt(1);
                role.name = rs.getString(2);
                role.description = rs.getString(3);
                role.attrIds = rs.getString(4);
                roles.add(role);
            }
            return roles;
        }
        catch(Exception e) {
            logger.error("failed to get roles", e);
            throw new DataException("failed to get roles");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Role> query(QueryTemplate<Role> template, Object... args) {
        return new ArrayList<Role>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Role role) {
        return role.id;
    }

    @Override
    public StringBuffer exportSingle(Role t) {
        StringBuffer sb = new StringBuffer("INSERT INTO roles (id, name, description, attr_ids) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.attrIds));
        sb.append(");");
        return sb;
    }

}
