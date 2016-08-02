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
import org.wilson.world.model.ImaginationItem;

import com.mysql.jdbc.Statement;

public class ImaginationItemDAO extends AbstractDAO<ImaginationItem> {
    public static final String ITEM_TABLE_NAME = "imagination_items";
    
    private static final Logger logger = Logger.getLogger(ImaginationItemDAO.class);

    @Override
    public void create(ImaginationItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.name)) {
            throw new DataException("item should have a valid name");
        }
        if(StringUtils.isBlank(item.content)) {
            throw new DataException("item should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into imagination_items(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.name);
            ps.setString(2, item.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                item.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create item", e);
            throw new DataException("failed to create item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ImaginationItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.name)) {
            throw new DataException("item should have a valid name");
        }
        if(StringUtils.isBlank(item.content)) {
            throw new DataException("item should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update imagination_items set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, item.name);
            ps.setString(2, item.content);
            ps.setInt(3, item.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update item", e);
            throw new DataException("failed to update item");
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
            String sql = "delete from imagination_items where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete item", e);
            throw new DataException("failed to delete item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ImaginationItem get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from imagination_item where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ImaginationItem item = new ImaginationItem();
                item.id = id;
                item.name = rs.getString(2);
                item.content = rs.getString(3);
                return item;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get item", e);
            throw new DataException("failed to get item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImaginationItem> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from imagination_items;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ImaginationItem> items = new ArrayList<ImaginationItem>();
            while(rs.next()) {
                ImaginationItem item = new ImaginationItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.content = rs.getString(3);
                items.add(item);
            }
            return items;
        }
        catch(Exception e) {
            logger.error("failed to get items", e);
            throw new DataException("failed to get items");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImaginationItem> query(QueryTemplate<ImaginationItem> template, Object... args) {
        return new ArrayList<ImaginationItem>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ImaginationItem item) {
        return item.id;
    }

    @Override
    public StringBuffer exportSingle(ImaginationItem t) {
        StringBuffer sb = new StringBuffer("INSERT INTO imagination_items (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.content));
        sb.append("');");
        return sb;
    }

}
