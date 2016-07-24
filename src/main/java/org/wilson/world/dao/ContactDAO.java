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
import org.wilson.world.model.Contact;

import com.mysql.jdbc.Statement;

public class ContactDAO extends AbstractDAO<Contact> {
    public static final String ITEM_TABLE_NAME = "contacts";
    
    private static final Logger logger = Logger.getLogger(ContactDAO.class);

    @Override
    public void create(Contact contact) {
        if(contact == null) {
            throw new DataException("Contact should not be null");
        }
        if(StringUtils.isBlank(contact.name)) {
            throw new DataException("Contact should have a valid name");
        }
        if(StringUtils.isBlank(contact.content)) {
            throw new DataException("Contact should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into contacts(name, content, created_time, modified_time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, contact.name);
            ps.setString(2, contact.content);
            ps.setLong(3, contact.createdTime);
            ps.setLong(4, contact.modifiedTime);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                contact.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create contact", e);
            throw new DataException("failed to create contact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Contact contact) {
        if(contact == null) {
            throw new DataException("Contact should not be null");
        }
        if(StringUtils.isBlank(contact.name)) {
            throw new DataException("Contact should have a valid name");
        }
        if(StringUtils.isBlank(contact.content)) {
            throw new DataException("Contact should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update contacts set name = ?, content = ?, modified_time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, contact.name);
            ps.setString(2, contact.content);
            ps.setLong(3, contact.modifiedTime);
            ps.setInt(4, contact.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update contact", e);
            throw new DataException("failed to update contact");
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
            String sql = "delete from contacts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete contact", e);
            throw new DataException("failed to delete contact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Contact get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contacts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Contact contact = new Contact();
                contact.id = id;
                contact.name = rs.getString(2);
                contact.content = rs.getString(3);
                contact.createdTime = rs.getLong(4);
                contact.modifiedTime = rs.getLong(5);
                return contact;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get contact", e);
            throw new DataException("failed to get contact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Contact> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contacts;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Contact> contacts = new ArrayList<Contact>();
            while(rs.next()) {
                Contact contact = new Contact();
                contact.id = rs.getInt(1);
                contact.name = rs.getString(2);
                contact.content = rs.getString(3);
                contact.createdTime = rs.getLong(4);
                contact.modifiedTime = rs.getLong(5);
                contacts.add(contact);
            }
            return contacts;
        }
        catch(Exception e) {
            logger.error("failed to get contacts", e);
            throw new DataException("failed to get contacts");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Contact> query(QueryTemplate<Contact> template, Object... args) {
        return new ArrayList<Contact>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Contact contact) {
        return contact.id;
    }

    @Override
    public StringBuffer exportSingle(Contact t) {
        StringBuffer sb = new StringBuffer("INSERT INTO contacts (id, name, content, created_time, modified_time) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.content));
        sb.append("',");
        sb.append(t.createdTime);
        sb.append(",");
        sb.append(t.modifiedTime);
        sb.append(");");
        return sb;
    }

}
