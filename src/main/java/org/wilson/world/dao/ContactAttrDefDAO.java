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
import org.wilson.world.model.ContactAttrDef;

import com.mysql.jdbc.Statement;

public class ContactAttrDefDAO extends AbstractDAO<ContactAttrDef> {
    public static final String ITEM_TABLE_NAME = "contact_attr_defs";
    
    private static final Logger logger = Logger.getLogger(ContactAttrDefDAO.class);

    @Override
    public void create(ContactAttrDef def) {
        if(def == null) {
            throw new DataException("contact attr def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("contact attr def should have a valid name");
        }
        if(StringUtils.isBlank(def.type)) {
            throw new DataException("contact attr def should have a valid type");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("contact attr def should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into contact_attr_defs(name, type, description) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, def.name);
            ps.setString(2, def.type);
            ps.setString(3, def.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                def.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create contact attr def", e);
            throw new DataException("failed to create contact attr def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ContactAttrDef def) {
        if(def == null) {
            throw new DataException("contact attr def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("contact attr def should have a valid name");
        }
        if(StringUtils.isBlank(def.type)) {
            throw new DataException("contact attr def should have a valid type");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("contact attr def should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update contact_attr_defs set name = ?, type = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, def.name);
            ps.setString(2, def.type);
            ps.setString(3, def.description);
            ps.setInt(4, def.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update contact attr def", e);
            throw new DataException("failed to contact contact attr def");
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
            String sql = "delete from contact_attr_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete contact attr def", e);
            throw new DataException("failed to delete contact attr def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ContactAttrDef get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contact_attr_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ContactAttrDef def = new ContactAttrDef();
                def.id = id;
                def.name = rs.getString(2);
                def.type = rs.getString(3);
                def.description = rs.getString(4);
                return def;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get contact attr def", e);
            throw new DataException("failed to get contact attr def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ContactAttrDef> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contact_attr_defs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ContactAttrDef> defs = new ArrayList<ContactAttrDef>();
            while(rs.next()) {
                ContactAttrDef def = new ContactAttrDef();
                def.id = rs.getInt(1);
                def.name = rs.getString(2);
                def.type = rs.getString(3);
                def.description = rs.getString(4);
                defs.add(def);
            }
            return defs;
        }
        catch(Exception e) {
            logger.error("failed to get contact attr defs", e);
            throw new DataException("failed to get contact attr defs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ContactAttrDef> query(QueryTemplate<ContactAttrDef> template, Object... args) {
        return new ArrayList<ContactAttrDef>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ContactAttrDef def) {
        return def.id;
    }

    @Override
    public StringBuffer exportSingle(ContactAttrDef t) {
        StringBuffer sb = new StringBuffer("INSERT INTO contact_attr_defs (id, name, type, description) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(");");
        return sb;
    }

}
