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
import org.wilson.world.model.InventoryItem;

import com.mysql.jdbc.Statement;

public class InventoryItemDAO extends AbstractDAO<InventoryItem> {
    public static final String ITEM_TABLE_NAME = "inv_items";
    
    private static final Logger logger = Logger.getLogger(InventoryItemDAO.class);

    @Override
    public void create(InventoryItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.status)) {
            throw new DataException("item should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into inv_items(item_id, price, amount, status) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, item.itemId);
            ps.setInt(2, item.price);
            ps.setInt(3, item.amount);
            ps.setString(4, item.status);
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
    public void update(InventoryItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.status)) {
            throw new DataException("item should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update inv_items set item_id = ?, price = ?, amount = ?, status = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, item.itemId);
            ps.setInt(2, item.price);
            ps.setInt(3, item.amount);
            ps.setString(4, item.status);
            ps.setInt(5, item.id);
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
            String sql = "delete from inv_items where id = ?;";
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
    public InventoryItem get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from inv_items where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                InventoryItem item = new InventoryItem();
                item.id = id;
                item.itemId = rs.getInt(2);
                item.price = rs.getInt(3);
                item.amount = rs.getInt(4);
                item.status = rs.getString(5);
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
    public List<InventoryItem> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from inv_items;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<InventoryItem> items = new ArrayList<InventoryItem>();
            while(rs.next()) {
                InventoryItem item = new InventoryItem();
                item.id = rs.getInt(1);
                item.itemId = rs.getInt(2);
                item.price = rs.getInt(3);
                item.amount = rs.getInt(4);
                item.status = rs.getString(5);
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
    public List<InventoryItem> query(QueryTemplate<InventoryItem> template, Object... args) {
        return new ArrayList<InventoryItem>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(InventoryItem item) {
        return item.id;
    }

    @Override
    public StringBuffer exportSingle(InventoryItem t) {
        StringBuffer sb = new StringBuffer("INSERT INTO inv_items (id, item_id, price, amount, status) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.itemId);
        sb.append(",");
        sb.append(t.price);
        sb.append(",");
        sb.append(t.amount);
        sb.append(",'");
        sb.append(escape(t.status));
        sb.append("');");
        return sb;
    }

}
