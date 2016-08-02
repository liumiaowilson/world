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
import org.wilson.world.model.HumorPattern;

import com.mysql.jdbc.Statement;

public class HumorPatternDAO extends AbstractDAO<HumorPattern> {
    public static final String ITEM_TABLE_NAME = "humor_patterns";
    
    private static final Logger logger = Logger.getLogger(HumorPatternDAO.class);

    @Override
    public void create(HumorPattern pattern) {
        if(pattern == null) {
            throw new DataException("pattern should not be null");
        }
        if(StringUtils.isBlank(pattern.name)) {
            throw new DataException("pattern should have a valid name");
        }
        if(StringUtils.isBlank(pattern.content)) {
            throw new DataException("pattern should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into humor_patterns (name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pattern.name);
            ps.setString(2, pattern.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                pattern.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create pattern", e);
            throw new DataException("failed to create pattern");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(HumorPattern pattern) {
        if(pattern == null) {
            throw new DataException("pattern should not be null");
        }
        if(StringUtils.isBlank(pattern.name)) {
            throw new DataException("pattern should have a valid name");
        }
        if(StringUtils.isBlank(pattern.content)) {
            throw new DataException("pattern should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update humor_patterns set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, pattern.name);
            ps.setString(2, pattern.content);
            ps.setInt(3, pattern.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update pattern", e);
            throw new DataException("failed to update pattern");
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
            String sql = "delete from humor_patterns where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete pattern", e);
            throw new DataException("failed to delete pattern");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public HumorPattern get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from humor_patterns where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                HumorPattern pattern = new HumorPattern();
                pattern.id = id;
                pattern.name = rs.getString(2);
                pattern.content = rs.getString(3);
                return pattern;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get pattern", e);
            throw new DataException("failed to get pattern");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<HumorPattern> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from humor_patterns;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<HumorPattern> patterns = new ArrayList<HumorPattern>();
            while(rs.next()) {
                HumorPattern pattern = new HumorPattern();
                pattern.id = rs.getInt(1);
                pattern.name = rs.getString(2);
                pattern.content = rs.getString(3);
                patterns.add(pattern);
            }
            return patterns;
        }
        catch(Exception e) {
            logger.error("failed to get patterns", e);
            throw new DataException("failed to get patterns");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<HumorPattern> query(QueryTemplate<HumorPattern> template, Object... args) {
        return new ArrayList<HumorPattern>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(HumorPattern pattern) {
        return pattern.id;
    }

    @Override
    public StringBuffer exportSingle(HumorPattern t) {
        StringBuffer sb = new StringBuffer("INSERT INTO humor_patterns (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.content));
        sb.append("');");
        return sb;
    }

}
