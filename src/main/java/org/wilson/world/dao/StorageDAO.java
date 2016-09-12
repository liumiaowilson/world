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
import org.wilson.world.model.Storage;

import com.mysql.jdbc.Statement;

public class StorageDAO extends AbstractDAO<Storage> {
    public static final String ITEM_TABLE_NAME = "storages";
    
    private static final Logger logger = Logger.getLogger(StorageDAO.class);

    @Override
    public void create(Storage storage) {
        if(storage == null) {
            throw new DataException("storage should not be null");
        }
        if(StringUtils.isBlank(storage.name)) {
            throw new DataException("storage should have a valid name");
        }
        if(StringUtils.isBlank(storage.description)) {
            throw new DataException("storage should have a valid description");
        }
        if(StringUtils.isBlank(storage.url)) {
            throw new DataException("storage should have a valid url");
        }
        if(StringUtils.isBlank(storage.key)) {
            throw new DataException("storage should have a valid key");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into storages(name, description, url, key_str) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, storage.name);
            ps.setString(2, storage.description);
            ps.setString(3, storage.url);
            ps.setString(4, storage.key);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                storage.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create storage", e);
            throw new DataException("failed to create storage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Storage storage) {
        if(storage == null) {
            throw new DataException("storage should not be null");
        }
        if(StringUtils.isBlank(storage.name)) {
            throw new DataException("storage should have a valid name");
        }
        if(StringUtils.isBlank(storage.description)) {
            throw new DataException("storage should have a valid description");
        }
        if(StringUtils.isBlank(storage.url)) {
            throw new DataException("storage should have a valid url");
        }
        if(StringUtils.isBlank(storage.key)) {
            throw new DataException("storage should have a valid key");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update storages set name = ?, description = ?, url = ?, key_str = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, storage.name);
            ps.setString(2, storage.description);
            ps.setString(3, storage.url);
            ps.setString(4, storage.key);
            ps.setInt(5, storage.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update storage", e);
            throw new DataException("failed to update storage");
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
            String sql = "delete from storages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete storage", e);
            throw new DataException("failed to delete storage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Storage get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from storages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Storage storage = new Storage();
                storage.id = id;
                storage.name = rs.getString(2);
                storage.description = rs.getString(3);
                storage.url = rs.getString(4);
                storage.key = rs.getString(5);
                return storage;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get storage", e);
            throw new DataException("failed to get storage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Storage> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from storages;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Storage> storages = new ArrayList<Storage>();
            while(rs.next()) {
                Storage storage = new Storage();
                storage.id = rs.getInt(1);
                storage.name = rs.getString(2);
                storage.description = rs.getString(3);
                storage.url = rs.getString(4);
                storage.key = rs.getString(5);
                storages.add(storage);
            }
            return storages;
        }
        catch(Exception e) {
            logger.error("failed to get storages", e);
            throw new DataException("failed to get storages");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Storage> query(QueryTemplate<Storage> template, Object... args) {
        return new ArrayList<Storage>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Storage storage) {
        return storage.id;
    }

    @Override
    public StringBuffer exportSingle(Storage t) {
        StringBuffer sb = new StringBuffer("INSERT INTO storages (id, name, description, url, key_str) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.url));
        sb.append(",");
        sb.append(escapeStr(t.key));
        sb.append(");");
        return sb;
    }

}
