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
import org.wilson.world.model.ExpenseItem;

import com.mysql.jdbc.Statement;

public class ExpenseItemDAO extends AbstractDAO<ExpenseItem> {
    public static final String ITEM_TABLE_NAME = "expense_items";
    
    private static final Logger logger = Logger.getLogger(ExpenseItemDAO.class);

    @Override
    public void create(ExpenseItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.name)) {
            throw new DataException("item should have a valid name");
        }
        if(StringUtils.isBlank(item.type)) {
            throw new DataException("item should have a valid type");
        }
        if(StringUtils.isBlank(item.description)) {
            throw new DataException("item should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into expense_items (name, type, description, amount, time) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.name);
            ps.setString(2, item.type);
            ps.setString(3, item.description);
            ps.setInt(4, item.amount);
            ps.setLong(5, item.time);
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
    public void update(ExpenseItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(StringUtils.isBlank(item.name)) {
            throw new DataException("item should have a valid name");
        }
        if(StringUtils.isBlank(item.type)) {
            throw new DataException("item should have a valid type");
        }
        if(StringUtils.isBlank(item.description)) {
            throw new DataException("item should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update expense_items set name = ?, type = ?, description = ?, amount = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, item.name);
            ps.setString(2, item.type);
            ps.setString(3, item.description);
            ps.setInt(4, item.amount);
            ps.setLong(5, item.time);
            ps.setInt(6, item.id);
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
            String sql = "delete from expense_items where id = ?;";
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
    public ExpenseItem get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from expense_items where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ExpenseItem item = new ExpenseItem();
                item.id = id;
                item.name = rs.getString(2);
                item.type = rs.getString(3);
                item.description = rs.getString(4);
                item.amount = rs.getInt(5);
                item.time = rs.getLong(6);
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
    public List<ExpenseItem> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from expense_items;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ExpenseItem> items = new ArrayList<ExpenseItem>();
            while(rs.next()) {
                ExpenseItem item = new ExpenseItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.type = rs.getString(3);
                item.description = rs.getString(4);
                item.amount = rs.getInt(5);
                item.time = rs.getLong(6);
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
    public List<ExpenseItem> query(QueryTemplate<ExpenseItem> template, Object... args) {
        return new ArrayList<ExpenseItem>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ExpenseItem item) {
        return item.id;
    }

    @Override
    public StringBuffer exportSingle(ExpenseItem t) {
        StringBuffer sb = new StringBuffer("INSERT INTO expense_items (id, name, type, description, amount, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(t.amount);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
