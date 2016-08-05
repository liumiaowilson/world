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
import org.wilson.world.model.FestivalData;

import com.mysql.jdbc.Statement;

public class FestivalDataDAO extends AbstractDAO<FestivalData> {
    public static final String ITEM_TABLE_NAME = "festival_data";
    
    private static final Logger logger = Logger.getLogger(FestivalDataDAO.class);

    @Override
    public void create(FestivalData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.definition)) {
            throw new DataException("data should have a valid definition");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into festival_data(name, description, definition) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.definition);
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
    public void update(FestivalData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.definition)) {
            throw new DataException("data should have a valid definition");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update festival_data set name = ?, description = ?, definition = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.definition);
            ps.setInt(4, data.id);
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
            String sql = "delete from festival_data where id = ?;";
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
    public FestivalData get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from festival_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                FestivalData data = new FestivalData();
                data.id = id;
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.definition = rs.getString(4);
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
    public List<FestivalData> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from festival_data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<FestivalData> datas = new ArrayList<FestivalData>();
            while(rs.next()) {
                FestivalData data = new FestivalData();
                data.id = rs.getInt(1);
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.definition = rs.getString(4);
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
    public List<FestivalData> query(QueryTemplate<FestivalData> template, Object... args) {
        return new ArrayList<FestivalData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(FestivalData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(FestivalData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO festival_data (id, name, description, definition) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.description));
        sb.append("','");
        sb.append(escape(t.definition));
        sb.append("');");
        return sb;
    }

}
