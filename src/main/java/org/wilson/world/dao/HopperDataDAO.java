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
import org.wilson.world.model.HopperData;

import com.mysql.jdbc.Statement;

public class HopperDataDAO extends AbstractDAO<HopperData> {
    public static final String ITEM_TABLE_NAME = "hopper_data";
    
    private static final Logger logger = Logger.getLogger(HopperDataDAO.class);

    @Override
    public void create(HopperData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.status)) {
            throw new DataException("data should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into hopper_data(hopper_id, status, fail_count, last_time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, data.hopperId);
            ps.setString(2, data.status);
            ps.setInt(3, data.failCount);
            ps.setLong(4, data.lastTime);
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
    public void update(HopperData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.status)) {
            throw new DataException("data should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update hopper_data set hopper_id = ?, status = ?, fail_count = ?, last_time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, data.hopperId);
            ps.setString(2, data.status);
            ps.setInt(3, data.failCount);
            ps.setLong(4, data.lastTime);
            ps.setInt(5, data.id);
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
            String sql = "delete from hopper_data where id = ?;";
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
    public HopperData get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hopper_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                HopperData data = new HopperData();
                data.id = id;
                data.hopperId = rs.getInt(2);
                data.status = rs.getString(3);
                data.failCount = rs.getInt(4);
                data.lastTime = rs.getLong(5);
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
    public List<HopperData> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hopper_data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<HopperData> datas = new ArrayList<HopperData>();
            while(rs.next()) {
                HopperData data = new HopperData();
                data.id = rs.getInt(1);
                data.hopperId = rs.getInt(2);
                data.status = rs.getString(3);
                data.failCount = rs.getInt(4);
                data.lastTime = rs.getLong(5);
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
    public List<HopperData> query(QueryTemplate<HopperData> template, Object... args) {
        return new ArrayList<HopperData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(HopperData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(HopperData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO hopper_data (id, hopper_id, status, fail_count, last_time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.hopperId);
        sb.append(",'");
        sb.append(escape(t.status));
        sb.append("',");
        sb.append(t.failCount);
        sb.append(",");
        sb.append(t.lastTime);
        sb.append(");");
        return sb;
    }

}
