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
import org.wilson.world.item.DataSizeInfo;

import com.mysql.jdbc.Statement;

public class DataSizeInfoDAO extends AbstractDAO<DataSizeInfo> {
    public static final String ITEM_TABLE_NAME = "data_size_infos";
    
    private static final Logger logger = Logger.getLogger(DataSizeInfoDAO.class);

    @Override
    public void create(DataSizeInfo info) {
        if(info == null) {
            throw new DataException("info should not be null");
        }
        if(StringUtils.isBlank(info.name)) {
            throw new DataException("info should have a valid name");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into data_size_infos(name, size, time) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, info.name);
            ps.setInt(2, info.size);
            ps.setLong(3, info.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                info.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create info", e);
            throw new DataException("failed to create info");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(DataSizeInfo info) {
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public DataSizeInfo get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data_size_infos where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                DataSizeInfo info = new DataSizeInfo();
                info.id = id;
                info.name = rs.getString(2);
                info.size = rs.getInt(3);
                info.time = rs.getLong(4);
                return info;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get info", e);
            throw new DataException("failed to get info");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<DataSizeInfo> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data_size_infos;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<DataSizeInfo> infos = new ArrayList<DataSizeInfo>();
            while(rs.next()) {
                DataSizeInfo info = new DataSizeInfo();
                info.id = rs.getInt(1);
                info.name = rs.getString(2);
                info.size = rs.getInt(3);
                info.time = rs.getLong(4);
                infos.add(info);
            }
            return infos;
        }
        catch(Exception e) {
            logger.error("failed to get infos", e);
            throw new DataException("failed to get infos");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    
    public List<DataSizeInfo> getAllByName(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from data_size_infos where name = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            List<DataSizeInfo> infos = new ArrayList<DataSizeInfo>();
            while(rs.next()) {
                DataSizeInfo info = new DataSizeInfo();
                info.id = rs.getInt(1);
                info.name = rs.getString(2);
                info.size = rs.getInt(3);
                info.time = rs.getLong(4);
                infos.add(info);
            }
            return infos;
        }
        catch(Exception e) {
            logger.error("failed to get infos", e);
            throw new DataException("failed to get infos");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<DataSizeInfo> query(QueryTemplate<DataSizeInfo> template, Object... args) {
        return new ArrayList<DataSizeInfo>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(DataSizeInfo info) {
        return info.id;
    }

    @Override
    public StringBuffer exportSingle(DataSizeInfo t) {
        StringBuffer sb = new StringBuffer("INSERT INTO data_size_infos (id, name, size, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(t.size);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
