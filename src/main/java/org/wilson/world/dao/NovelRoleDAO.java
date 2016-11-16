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
import org.wilson.world.model.NovelRole;

import com.mysql.jdbc.Statement;

public class NovelRoleDAO extends AbstractDAO<NovelRole> {
    public static final String ITEM_TABLE_NAME = "novel_roles";
    
    private static final Logger logger = Logger.getLogger(NovelRoleDAO.class);

    @Override
    public void create(NovelRole role) {
        if(role == null) {
            throw new DataException("NovelRole should not be null");
        }
        if(StringUtils.isBlank(role.name)) {
            throw new DataException("NovelRole should have a valid name");
        }
        if(StringUtils.isBlank(role.description)) {
            throw new DataException("NovelRole should have a valid description");
        }
        if(StringUtils.isBlank(role.definition)) {
            throw new DataException("NovelRole should have a valid definition");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_roles(name, description, definition) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name);
            ps.setString(2, role.description);
            ps.setString(3, role.definition);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                role.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel role", e);
            throw new DataException("failed to create novel role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelRole role) {
    	if(role == null) {
            throw new DataException("NovelRole should not be null");
        }
        if(StringUtils.isBlank(role.name)) {
            throw new DataException("NovelRole should have a valid name");
        }
        if(StringUtils.isBlank(role.description)) {
            throw new DataException("NovelRole should have a valid description");
        }
        if(StringUtils.isBlank(role.definition)) {
            throw new DataException("NovelRole should have a valid definition");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_roles set name = ?, description = ?, definition = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, role.name);
            ps.setString(2, role.description);
            ps.setString(3, role.definition);
            ps.setInt(4, role.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel role", e);
            throw new DataException("failed to update novel role");
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
            String sql = "delete from novel_roles where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel role", e);
            throw new DataException("failed to delete novel role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelRole get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_roles where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelRole role = new NovelRole();
                role.id = id;
                role.name = rs.getString(2);
                role.description = rs.getString(3);
                role.definition = rs.getString(4);
                return role;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel role", e);
            throw new DataException("failed to get novel role");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelRole> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_roles;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelRole> roles = new ArrayList<NovelRole>();
            while(rs.next()) {
            	NovelRole role = new NovelRole();
                role.id = rs.getInt(1);
                role.name = rs.getString(2);
                role.description = rs.getString(3);
                role.definition = rs.getString(4);
                roles.add(role);
            }
            return roles;
        }
        catch(Exception e) {
            logger.error("failed to get novel roles", e);
            throw new DataException("failed to get novel roles");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelRole> query(QueryTemplate<NovelRole> template, Object... args) {
        return new ArrayList<NovelRole>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelRole role) {
        return role.id;
    }

    @Override
    public StringBuffer exportSingle(NovelRole t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_roles (id, name, description, definition) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.definition));
        sb.append(");");
        return sb;
    }

}
