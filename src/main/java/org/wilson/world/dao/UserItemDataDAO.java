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
import org.wilson.world.model.UserItemData;

import com.mysql.jdbc.Statement;

public class UserItemDataDAO extends AbstractDAO<UserItemData> {
    public static final String ITEM_TABLE_NAME = "user_item_data";
    
    private static final Logger logger = Logger.getLogger(UserItemDataDAO.class);

    @Override
    public void create(UserItemData data) {
        if(data == null) {
            throw new DataException("user item data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("user item data should have a valid name");
        }
        if(StringUtils.isBlank(data.type)) {
            throw new DataException("user item data should have a valid type");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("user item data should have a valid description");
        }
        if(StringUtils.isBlank(data.effect)) {
            throw new DataException("user item data should have a valid effect");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into user_item_data(name, type, description, effect, value) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.name);
            ps.setString(2, data.type);
            ps.setString(3, data.description);
            ps.setString(4, data.effect);
            ps.setInt(5, data.value);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                data.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
            throw new DataException("failed to create data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(UserItemData data) {
        if(data == null) {
            throw new DataException("user item data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("user item data should have a valid name");
        }
        if(StringUtils.isBlank(data.type)) {
            throw new DataException("user item data should have a valid type");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("user item data should have a valid description");
        }
        if(StringUtils.isBlank(data.effect)) {
            throw new DataException("user item data should have a valid effect");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update user_item_data set name = ?, type = ?, description = ?, effect = ?, value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, data.name);
            ps.setString(2, data.type);
            ps.setString(3, data.description);
            ps.setString(4, data.effect);
            ps.setInt(5, data.value);
            ps.setInt(6, data.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
            throw new DataException("failed to update data");
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
            String sql = "delete from user_item_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            throw new DataException("failed to delete data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public UserItemData get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from user_item_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                UserItemData data = new UserItemData();
                data.id = id;
                data.name = rs.getString(2);
                data.type = rs.getString(3);
                data.description = rs.getString(4);
                data.effect = rs.getString(5);
                data.value = rs.getInt(6);
                return data;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
            throw new DataException("failed to get data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<UserItemData> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from user_item_data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<UserItemData> datas = new ArrayList<UserItemData>();
            while(rs.next()) {
                UserItemData data = new UserItemData();
                data.id = rs.getInt(1);
                data.name = rs.getString(2);
                data.type = rs.getString(3);
                data.description = rs.getString(4);
                data.effect = rs.getString(5);
                data.value = rs.getInt(6);
                datas.add(data);
            }
            return datas;
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
            throw new DataException("failed to get datas");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<UserItemData> query(QueryTemplate<UserItemData> template, Object... args) {
        return new ArrayList<UserItemData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(UserItemData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(UserItemData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO user_item_data (id, name, type, description, effect, value) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.effect));
        sb.append(",");
        sb.append(t.value);
        sb.append(");");
        return sb;
    }

}
