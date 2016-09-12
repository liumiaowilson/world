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
import org.wilson.world.model.ContactAttr;

import com.mysql.jdbc.Statement;

public class ContactAttrDAO extends AbstractDAO<ContactAttr> {
    public static final String ITEM_TABLE_NAME = "contact_attrs";
    
    private static final Logger logger = Logger.getLogger(ContactAttrDAO.class);
    
    @Override
    public void create(ContactAttr attr) {
        if(attr == null) {
            throw new DataException("Contact attribute should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("Contact attribute should have a valid name");
        }
        if(StringUtils.isBlank(attr.value)) {
            throw new DataException("Contact attribute should have a valid value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into contact_attrs(contact_id, name, value) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, attr.contactId);
            ps.setString(2, attr.name);
            ps.setString(3, attr.value);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                attr.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create contact attribute", e);
            throw new DataException("failed to create contact attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ContactAttr attr) {
        if(attr == null) {
            throw new DataException("Contact attribute should not be null");
        }
        if(StringUtils.isBlank(attr.name)) {
            throw new DataException("Contact attribute should have a valid name");
        }
        if(StringUtils.isBlank(attr.value)) {
            throw new DataException("Contact attribute should have a valid value");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update contact_attrs set contact_id = ?, name = ?, value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, attr.contactId);
            ps.setString(2, attr.name);
            ps.setString(3, attr.value);
            ps.setInt(4, attr.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update contact attribute", e);
            throw new DataException("failed to update contact attribute");
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
            String sql = "delete from contact_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete contact attribute", e);
            throw new DataException("failed to delete contact attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ContactAttr get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contact_attrs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ContactAttr attr = new ContactAttr();
                attr.id = id;
                attr.contactId = rs.getInt(2);
                attr.name = rs.getString(3);
                attr.value = rs.getString(4);
                return attr;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get contact attribute", e);
            throw new DataException("failed to get contact attribute");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ContactAttr> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contact_attrs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ContactAttr> attrs = new ArrayList<ContactAttr>();
            while(rs.next()) {
                ContactAttr attr = new ContactAttr();
                attr.id = rs.getInt(1);
                attr.contactId = rs.getInt(2);
                attr.name = rs.getString(3);
                attr.value = rs.getString(4);
                attrs.add(attr);
            }
            return attrs;
        }
        catch(Exception e) {
            logger.error("failed to get contact attrs", e);
            throw new DataException("failed to get contact attrs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ContactAttr> query(QueryTemplate<ContactAttr> template, Object... args) {
        return new ArrayList<ContactAttr>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ContactAttr attr) {
        return attr.id;
    }

    @Override
    public StringBuffer exportSingle(ContactAttr t) {
        StringBuffer sb = new StringBuffer("INSERT INTO contact_attrs (id, contact_id, name, value) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.contactId);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.value));
        sb.append(");");
        return sb;
    }

}
