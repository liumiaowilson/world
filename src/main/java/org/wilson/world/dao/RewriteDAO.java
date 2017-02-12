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
import org.wilson.world.model.Rewrite;

import com.mysql.jdbc.Statement;

public class RewriteDAO extends AbstractDAO<Rewrite> {
    public static final String ITEM_TABLE_NAME = "rewrites";
    
    private static final Logger logger = Logger.getLogger(RewriteDAO.class);

    @Override
    public void create(Rewrite rewrite) {
        if(rewrite == null) {
            throw new DataException("Rewrite should not be null");
        }
        if(StringUtils.isBlank(rewrite.name)) {
            throw new DataException("Rewrite should have a valid name");
        }
        if(StringUtils.isBlank(rewrite.regex)) {
            throw new DataException("Rewrite should have a valid regex");
        }
        if(StringUtils.isBlank(rewrite.fromUrl)) {
            throw new DataException("Rewrite should have a valid fromUrl");
        }
        if(StringUtils.isBlank(rewrite.toUrl)) {
            throw new DataException("Rewrite should have a valid toUrl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into rewrites(name, regex, from_url, to_url) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, rewrite.name);
            ps.setString(2, rewrite.regex);
            ps.setString(3, rewrite.fromUrl);
            ps.setString(4, rewrite.toUrl);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                rewrite.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create rewrite", e);
            throw new DataException("failed to create rewrite");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Rewrite rewrite) {
        if(rewrite == null) {
            throw new DataException("Rewrite should not be null");
        }
        if(StringUtils.isBlank(rewrite.name)) {
            throw new DataException("Rewrite should have a valid name");
        }
        if(StringUtils.isBlank(rewrite.regex)) {
            throw new DataException("Rewrite should have a valid regex");
        }
        if(StringUtils.isBlank(rewrite.fromUrl)) {
            throw new DataException("Rewrite should have a valid fromUrl");
        }
        if(StringUtils.isBlank(rewrite.toUrl)) {
            throw new DataException("Rewrite should have a valid toUrl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update rewrites set name = ?, regex = ?, from_url = ?, to_url = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, rewrite.name);
            ps.setString(2, rewrite.regex);
            ps.setString(3, rewrite.fromUrl);
            ps.setString(4, rewrite.toUrl);
            ps.setInt(5, rewrite.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update rewrite", e);
            throw new DataException("failed to update rewrite");
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
            String sql = "delete from rewrites where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete rewrite", e);
            throw new DataException("failed to delete rewrite");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Rewrite get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from rewrites where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Rewrite rewrite = new Rewrite();
                rewrite.id = id;
                rewrite.name = rs.getString(2);
                rewrite.regex = rs.getString(3);
                rewrite.fromUrl = rs.getString(4);
                rewrite.toUrl = rs.getString(5);
                return rewrite;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get rewrite", e);
            throw new DataException("failed to get rewrite");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Rewrite> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from rewrites;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Rewrite> rewrites = new ArrayList<Rewrite>();
            while(rs.next()) {
            	Rewrite rewrite = new Rewrite();
                rewrite.id = rs.getInt(1);
                rewrite.name = rs.getString(2);
                rewrite.regex = rs.getString(3);
                rewrite.fromUrl = rs.getString(4);
                rewrite.toUrl = rs.getString(5);
                rewrites.add(rewrite);
            }
            return rewrites;
        }
        catch(Exception e) {
            logger.error("failed to get rewrites", e);
            throw new DataException("failed to get rewrites");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Rewrite> query(QueryTemplate<Rewrite> template, Object... args) {
        return new ArrayList<Rewrite>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Rewrite rewrite) {
        return rewrite.id;
    }

    @Override
    public StringBuffer exportSingle(Rewrite t) {
        StringBuffer sb = new StringBuffer("INSERT INTO rewrites (id, name, regex, from_url, to_url) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.regex));
        sb.append(",");
        sb.append(escapeStr(t.fromUrl));
        sb.append(",");
        sb.append(escapeStr(t.toUrl));
        sb.append(");");
        return sb;
    }

}
