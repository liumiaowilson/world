package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.DataItem;

public class DataItemDAO extends AbstractDAO<DataItem> {
    public static final String TABLE_NAME = "data";
    
    private static final Logger logger = Logger.getLogger(DataItemDAO.class);

    public DataItemDAO() {
        this.addQueryTemplate(new DataItemQueryByNameTemplate());
    }
    
    private void validateDataItem(DataItem item) {
        if(item == null) {
            throw new DataException("item should not be null");
        }
        if(item.name == null || item.name.length() > 20) {
            throw new DataException("Item name is invalid");
        }
        if(item.value == null || item.value.length() > 100) {
            throw new DataException("Item value is invalid");
        }
    }
    
    
    @Override
    public void create(DataItem item) {
        validateDataItem(item);
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into data(name, value) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.name);
            ps.setString(2, item.value);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                item.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create data item", e);
            throw new DataException("failed to create data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(DataItem item) {
        validateDataItem(item);
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update data set name = ?, value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, item.name);
            ps.setString(2, item.value);
            ps.setInt(3, item.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update data item", e);
            throw new DataException("failed to update data item");
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
            String sql = "delete from data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete data item", e);
            throw new DataException("failed to delete data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public DataItem get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                DataItem item = new DataItem();
                item.id = id;
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                return item;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data item", e);
            throw new DataException("failed to get data item");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<DataItem> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<DataItem> items = new ArrayList<DataItem>();
            while(rs.next()) {
                DataItem item = new DataItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                items.add(item);
            }
            return items;
        }
        catch(Exception e) {
            logger.error("failed to get data items", e);
            throw new DataException("failed to get data items");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<DataItem> query(QueryTemplate<DataItem> template, Object... args) {
        if(template == null) {
            return new ArrayList<DataItem>();
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = template.getSQL();
            ps = con.prepareStatement(sql);
            if(template.getQueryHelper() != null) {
                template.getQueryHelper().configurePreparedStatement(ps, args);
            }
            rs = ps.executeQuery();
            List<DataItem> items = new ArrayList<DataItem>();
            while(rs.next()) {
                DataItem item = new DataItem();
                item.id = rs.getInt(1);
                item.name = rs.getString(2);
                item.value = rs.getString(3);
                items.add(item);
            }
            return items;
        }
        catch(Exception e) {
            logger.error("failed to get data items", e);
            throw new DataException("failed to get data items");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public String getItemTableName() {
        return TABLE_NAME;
    }
    
    @Override
    public int getId(DataItem item) {
        return item.id;
    }

    public static class DataItemQueryByNameTemplate implements QueryTemplate<DataItem> {
        public static final String NAME = "data_item_query_by_name";
        
        private QueryHelper helper = new QueryHelper() {

            @Override
            public void configurePreparedStatement(PreparedStatement ps, Object... args) throws SQLException {
                String name = (String) args[0];
                ps.setString(1, name);
            }
            
        };
        
        @Override
        public String getID() {
            return NAME;
        }

        @Override
        public String getSQL() {
            return "select * from data where name = ?;";
        }

        @Override
        public QueryHelper getQueryHelper() {
            return helper;
        }

        @Override
        public boolean accept(DataItem t, Object... args) {
            String name = (String) args[0];
            return t.name.equals(name);
        }
    }
}
