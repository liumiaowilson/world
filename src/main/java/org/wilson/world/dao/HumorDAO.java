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
import org.wilson.world.model.Humor;

import com.mysql.jdbc.Statement;

public class HumorDAO extends AbstractDAO<Humor> {
    public static final String ITEM_TABLE_NAME = "humors";
    
    private static final Logger logger = Logger.getLogger(HumorDAO.class);

    @Override
    public void create(Humor humor) {
        if(humor == null) {
            throw new DataException("humor should not be null");
        }
        if(StringUtils.isBlank(humor.name)) {
            throw new DataException("humor should have a valid name");
        }
        if(StringUtils.isBlank(humor.content)) {
            throw new DataException("humor should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into humors(name, pattern_id, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, humor.name);
            ps.setInt(2, humor.patternId);
            ps.setString(3, humor.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                humor.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create humor", e);
            throw new DataException("failed to create humor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Humor humor) {
        if(humor == null) {
            throw new DataException("humor should not be null");
        }
        if(StringUtils.isBlank(humor.name)) {
            throw new DataException("humor should have a valid name");
        }
        if(StringUtils.isBlank(humor.content)) {
            throw new DataException("humor should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update humors set name = ?, pattern_id = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, humor.name);
            ps.setInt(2, humor.patternId);
            ps.setString(3, humor.content);
            ps.setInt(4, humor.id);
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
            String sql = "delete from humors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete humor", e);
            throw new DataException("failed to delete humor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Humor get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from humors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Humor humor = new Humor();
                humor.id = id;
                humor.name = rs.getString(2);
                humor.patternId = rs.getInt(3);
                humor.content = rs.getString(4);
                return humor;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get humor", e);
            throw new DataException("failed to get humor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Humor> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from humors;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Humor> humors = new ArrayList<Humor>();
            while(rs.next()) {
                Humor humor = new Humor();
                humor.id = rs.getInt(1);
                humor.name = rs.getString(2);
                humor.patternId = rs.getInt(3);
                humor.content = rs.getString(4);
                humors.add(humor);
            }
            return humors;
        }
        catch(Exception e) {
            logger.error("failed to get humors", e);
            throw new DataException("failed to get humors");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Humor> query(QueryTemplate<Humor> template, Object... args) {
        return new ArrayList<Humor>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Humor humor) {
        return humor.id;
    }

    @Override
    public StringBuffer exportSingle(Humor t) {
        StringBuffer sb = new StringBuffer("INSERT INTO humors (id, name, pattern_id, content) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("',");
        sb.append(t.patternId);
        sb.append(",'");
        sb.append(escape(t.content));
        sb.append("');");
        return sb;
    }

}
