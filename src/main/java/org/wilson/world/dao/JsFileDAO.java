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
import org.wilson.world.model.JsFile;

import com.mysql.jdbc.Statement;

public class JsFileDAO extends AbstractDAO<JsFile> {
    public static final String ITEM_TABLE_NAME = "js_files";
    
    private static final Logger logger = Logger.getLogger(JsFileDAO.class);

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public boolean isLoaded(JsFile t) {
        return t.source != null || t.test != null;
    }

    @Override
    public JsFile load(JsFile t) {
        return super.load(t);
    }

    @Override
    public JsFile unload(JsFile t) {
        t.source = null;
        t.test = null;
        return t;
    }
    
    @Override
    public void create(JsFile file) {
        if(file == null) {
            throw new DataException("JsFile should not be null");
        }
        if(StringUtils.isBlank(file.name)) {
            throw new DataException("JsFile should have a valid name");
        }
        if(StringUtils.isBlank(file.description)) {
            throw new DataException("JsFile should have a valid description");
        }
        if(StringUtils.isBlank(file.status)) {
            throw new DataException("JsFile should have a valid status");
        }
        if(StringUtils.isBlank(file.source)) {
            throw new DataException("JsFile should have a valid source");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into js_files(name, description, status, source, test) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, file.name);
            ps.setString(2, file.description);
            ps.setString(3, file.status);
            ps.setString(4, file.source);
            ps.setString(5, file.test);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                file.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create js file", e);
            throw new DataException("failed to create js file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(JsFile file) {
    	if(file == null) {
            throw new DataException("JsFile should not be null");
        }
        if(StringUtils.isBlank(file.name)) {
            throw new DataException("JsFile should have a valid name");
        }
        if(StringUtils.isBlank(file.description)) {
            throw new DataException("JsFile should have a valid description");
        }
        if(StringUtils.isBlank(file.status)) {
            throw new DataException("JsFile should have a valid status");
        }
        if(StringUtils.isBlank(file.source)) {
            throw new DataException("JsFile should have a valid source");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update js_files set name = ?, description = ?, status = ?, source = ?, test = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, file.name);
            ps.setString(2, file.description);
            ps.setString(3, file.status);
            ps.setString(4, file.source);
            ps.setString(5, file.test);
            ps.setInt(6, file.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update js file", e);
            throw new DataException("failed to update js file");
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
            String sql = "delete from js_files where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete js file", e);
            throw new DataException("failed to delete js file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public JsFile get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from js_files where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	JsFile file = new JsFile();
                file.id = id;
                file.name = rs.getString(2);
                file.description = rs.getString(3);
                file.status = rs.getString(4);
                if(!lazy) {
                	file.source = rs.getString(5);
                	file.test = rs.getString(6);
                }
                return file;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get js file", e);
            throw new DataException("failed to get js file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<JsFile> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from js_files;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<JsFile> files = new ArrayList<JsFile>();
            while(rs.next()) {
            	JsFile file = new JsFile();
                file.id = rs.getInt(1);
                file.name = rs.getString(2);
                file.description = rs.getString(3);
                file.status = rs.getString(4);
                if(!lazy) {
                	file.source = rs.getString(5);
                	file.test = rs.getString(6);
                }
                files.add(file);
            }
            return files;
        }
        catch(Exception e) {
            logger.error("failed to get js files", e);
            throw new DataException("failed to get js files");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<JsFile> query(QueryTemplate<JsFile> template, Object... args) {
        return new ArrayList<JsFile>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(JsFile file) {
        return file.id;
    }

    @Override
    public StringBuffer exportSingle(JsFile t) {
        StringBuffer sb = new StringBuffer("INSERT INTO js_files (id, name, description, status, source, test) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.status));
        sb.append(",");
        sb.append(escapeStr(t.source));
        sb.append(",");
        sb.append(escapeStr(t.test));
        sb.append(");");
        return sb;
    }

}
