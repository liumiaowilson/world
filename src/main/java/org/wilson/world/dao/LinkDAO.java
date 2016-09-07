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
import org.wilson.world.model.Link;

import com.mysql.jdbc.Statement;

public class LinkDAO extends AbstractDAO<Link> {
    public static final String ITEM_TABLE_NAME = "links";
    
    private static final Logger logger = Logger.getLogger(LinkDAO.class);

    @Override
    public void create(Link link) {
        if(link == null) {
            throw new DataException("Link should not be null");
        }
        if(StringUtils.isBlank(link.name)) {
            throw new DataException("Link should have a valid name");
        }
        if(StringUtils.isBlank(link.label)) {
            throw new DataException("Link should have a valid label");
        }
        if(StringUtils.isBlank(link.itemType)) {
            throw new DataException("Link should have a valid itemType");
        }
        if(StringUtils.isBlank(link.menuId)) {
            throw new DataException("Link should have a valid menuId");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into links(name, label, item_type, item_id, menu_id) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, link.name);
            ps.setString(2, link.label);
            ps.setString(3, link.itemType);
            ps.setInt(4, link.itemId);
            ps.setString(5, link.menuId);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                link.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create link", e);
            throw new DataException("failed to create link");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Link link) {
        if(link == null) {
            throw new DataException("Link should not be null");
        }
        if(StringUtils.isBlank(link.name)) {
            throw new DataException("Link should have a valid name");
        }
        if(StringUtils.isBlank(link.label)) {
            throw new DataException("Link should have a valid label");
        }
        if(StringUtils.isBlank(link.itemType)) {
            throw new DataException("Link should have a valid itemType");
        }
        if(StringUtils.isBlank(link.menuId)) {
            throw new DataException("Link should have a valid menuId");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update links set name = ?, label = ?, item_type = ?, item_id = ?, menu_id = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, link.name);
            ps.setString(2, link.label);
            ps.setString(3, link.itemType);
            ps.setInt(4, link.itemId);
            ps.setString(5, link.menuId);
            ps.setInt(6, link.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update link", e);
            throw new DataException("failed to update link");
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
            String sql = "delete from links where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete link", e);
            throw new DataException("failed to delete link");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Link get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from links where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Link link = new Link();
                link.id = id;
                link.name = rs.getString(2);
                link.label = rs.getString(3);
                link.itemType = rs.getString(4);
                link.itemId = rs.getInt(5);
                link.menuId = rs.getString(6);
                return link;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get link", e);
            throw new DataException("failed to get link");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Link> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from links;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Link> links = new ArrayList<Link>();
            while(rs.next()) {
                Link link = new Link();
                link.id = rs.getInt(1);
                link.name = rs.getString(2);
                link.label = rs.getString(3);
                link.itemType = rs.getString(4);
                link.itemId = rs.getInt(5);
                link.menuId = rs.getString(6);
                links.add(link);
            }
            return links;
        }
        catch(Exception e) {
            logger.error("failed to get links", e);
            throw new DataException("failed to get links");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Link> query(QueryTemplate<Link> template, Object... args) {
        return new ArrayList<Link>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Link link) {
        return link.id;
    }

    @Override
    public StringBuffer exportSingle(Link t) {
        StringBuffer sb = new StringBuffer("INSERT INTO links (id, name, label, item_type, item_id, menu_id) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.label));
        sb.append(",");
        sb.append(escapeStr(t.itemType));
        sb.append(",");
        sb.append(t.itemId);
        sb.append(",");
        sb.append(t.menuId);
        sb.append(");");
        return sb;
    }

}
