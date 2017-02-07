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
import org.wilson.world.model.JavaFile;

import com.mysql.jdbc.Statement;

public class JavaFileDAO extends AbstractDAO<JavaFile> {
    public static final String ITEM_TABLE_NAME = "java_files";
    
    private static final Logger logger = Logger.getLogger(JavaFileDAO.class);

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public boolean isLoaded(JavaFile t) {
        return t.source != null;
    }

    @Override
    public JavaFile load(JavaFile t) {
        return super.load(t);
    }

    @Override
    public JavaFile unload(JavaFile t) {
        t.source = null;
        return t;
    }
    
    @Override
    public void create(JavaFile file) {
        if(file == null) {
            throw new DataException("JavaFile should not be null");
        }
        if(StringUtils.isBlank(file.name)) {
            throw new DataException("JavaFile should have a valid name");
        }
        if(StringUtils.isBlank(file.description)) {
            throw new DataException("JavaFile should have a valid description");
        }
        if(StringUtils.isBlank(file.source)) {
            throw new DataException("JavaFile should have a valid source");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into java_files(name, description, source) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, file.name);
            ps.setString(2, file.description);
            ps.setString(3, file.source);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                file.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create java file", e);
            throw new DataException("failed to create java file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(JavaFile file) {
    	if(file == null) {
            throw new DataException("JavaFile should not be null");
        }
        if(StringUtils.isBlank(file.name)) {
            throw new DataException("JavaFile should have a valid name");
        }
        if(StringUtils.isBlank(file.description)) {
            throw new DataException("JavaFile should have a valid description");
        }
        if(StringUtils.isBlank(file.source)) {
            throw new DataException("JavaFile should have a valid source");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update java_files set name = ?, description = ?, source = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, file.name);
            ps.setString(2, file.description);
            ps.setString(3, file.source);
            ps.setInt(4, file.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update java file", e);
            throw new DataException("failed to update java file");
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
            String sql = "delete from java_files where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete java file", e);
            throw new DataException("failed to delete java file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public JavaFile get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from java_files where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	JavaFile file = new JavaFile();
                file.id = id;
                file.name = rs.getString(2);
                file.description = rs.getString(3);
                if(!lazy) {
                	file.source = rs.getString(4);
                }
                return file;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get java file", e);
            throw new DataException("failed to get java file");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<JavaFile> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from java_files;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<JavaFile> files = new ArrayList<JavaFile>();
            while(rs.next()) {
            	JavaFile file = new JavaFile();
                file.id = rs.getInt(1);
                file.name = rs.getString(2);
                file.description = rs.getString(3);
                if(!lazy) {
                	file.source = rs.getString(4);
                }
                files.add(file);
            }
            return files;
        }
        catch(Exception e) {
            logger.error("failed to get java files", e);
            throw new DataException("failed to get java files");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<JavaFile> query(QueryTemplate<JavaFile> template, Object... args) {
        return new ArrayList<JavaFile>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(JavaFile file) {
        return file.id;
    }

    @Override
    public StringBuffer exportSingle(JavaFile t) {
        StringBuffer sb = new StringBuffer("INSERT INTO java_files (id, name, description, source) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.source));
        sb.append(");");
        return sb;
    }

}
