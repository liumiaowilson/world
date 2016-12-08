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
import org.wilson.world.model.CloudStorageData;

import com.mysql.jdbc.Statement;

public class CloudStorageDataDAO extends AbstractDAO<CloudStorageData> {
    public static final String ITEM_TABLE_NAME = "cloud_storage_datas";
    
    private static final Logger logger = Logger.getLogger(CloudStorageDataDAO.class);

    @Override
    public void create(CloudStorageData data) {
        if(data == null) {
            throw new DataException("CloudStorageData should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("CloudStorageData should have a valid name");
        }
        if(StringUtils.isBlank(data.service)) {
            throw new DataException("CloudStorageData should have a valid service");
        }
        if(StringUtils.isBlank(data.content)) {
            throw new DataException("CloudStorageData should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into cloud_storage_datas(name, service, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.name);
            ps.setString(2, data.service);
            ps.setString(3, data.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                data.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create cloud storage data", e);
            throw new DataException("failed to create cloud storage data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(CloudStorageData data) {
    	if(data == null) {
            throw new DataException("CloudStorageData should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("CloudStorageData should have a valid name");
        }
        if(StringUtils.isBlank(data.service)) {
            throw new DataException("CloudStorageData should have a valid service");
        }
        if(StringUtils.isBlank(data.content)) {
            throw new DataException("CloudStorageData should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update cloud_storage_datas set name = ?, service = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, data.name);
            ps.setString(2, data.service);
            ps.setString(3, data.content);
            ps.setInt(4, data.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update cloud storage data", e);
            throw new DataException("failed to update cloud storage data");
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
            String sql = "delete from cloud_storage_datas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete cloud storage data", e);
            throw new DataException("failed to delete cloud storage data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public CloudStorageData get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from cloud_storage_datas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	CloudStorageData data = new CloudStorageData();
                data.id = id;
                data.name = rs.getString(2);
                data.service = rs.getString(3);
                data.content = rs.getString(4);
                return data;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get cloud storage data", e);
            throw new DataException("failed to get cloud storage data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CloudStorageData> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from cloud_storage_datas;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<CloudStorageData> datas = new ArrayList<CloudStorageData>();
            while(rs.next()) {
            	CloudStorageData data = new CloudStorageData();
                data.id = rs.getInt(1);
                data.name = rs.getString(2);
                data.service = rs.getString(3);
                data.content = rs.getString(4);
                datas.add(data);
            }
            return datas;
        }
        catch(Exception e) {
            logger.error("failed to get cloud storage datas", e);
            throw new DataException("failed to get cloud storage datas");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CloudStorageData> query(QueryTemplate<CloudStorageData> template, Object... args) {
        return new ArrayList<CloudStorageData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(CloudStorageData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(CloudStorageData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO cloud_storage_datas (id, name, service, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.service));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
