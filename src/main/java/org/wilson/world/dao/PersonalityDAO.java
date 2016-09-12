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
import org.wilson.world.model.Personality;

import com.mysql.jdbc.Statement;

public class PersonalityDAO extends AbstractDAO<Personality> {
    public static final String ITEM_TABLE_NAME = "personalities";
    
    private static final Logger logger = Logger.getLogger(PersonalityDAO.class);

    @Override
    public void create(Personality personality) {
        if(personality == null) {
            throw new DataException("Personality should not be null");
        }
        if(StringUtils.isBlank(personality.name)) {
            throw new DataException("Personality should have a valid name");
        }
        if(StringUtils.isBlank(personality.tags)) {
            throw new DataException("Personality should have a valid tags");
        }
        if(StringUtils.isBlank(personality.description)) {
            throw new DataException("Personality should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into personalities(name, tags, description) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, personality.name);
            ps.setString(2, personality.tags);
            ps.setString(3, personality.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                personality.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create personality", e);
            throw new DataException("failed to create personality");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Personality personality) {
        if(personality == null) {
            throw new DataException("Personality should not be null");
        }
        if(StringUtils.isBlank(personality.name)) {
            throw new DataException("Personality should have a valid name");
        }
        if(StringUtils.isBlank(personality.tags)) {
            throw new DataException("Personality should have a valid tags");
        }
        if(StringUtils.isBlank(personality.description)) {
            throw new DataException("Personality should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update personalities set name = ?, tags = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, personality.name);
            ps.setString(2, personality.tags);
            ps.setString(3, personality.description);
            ps.setInt(4, personality.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update personality", e);
            throw new DataException("failed to update personality");
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
            String sql = "delete from personalities where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete personality", e);
            throw new DataException("failed to delete personality");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Personality get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from personalities where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Personality personality = new Personality();
                personality.id = id;
                personality.name = rs.getString(2);
                personality.tags = rs.getString(3);
                personality.description = rs.getString(4);
                return personality;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get personality", e);
            throw new DataException("failed to get personality");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Personality> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from personalities;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Personality> personalities = new ArrayList<Personality>();
            while(rs.next()) {
                Personality personality = new Personality();
                personality.id = rs.getInt(1);
                personality.name = rs.getString(2);
                personality.tags = rs.getString(3);
                personality.description = rs.getString(4);
                personalities.add(personality);
            }
            return personalities;
        }
        catch(Exception e) {
            logger.error("failed to get personalities", e);
            throw new DataException("failed to get personalities");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Personality> query(QueryTemplate<Personality> template, Object... args) {
        return new ArrayList<Personality>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Personality personality) {
        return personality.id;
    }

    @Override
    public StringBuffer exportSingle(Personality t) {
        StringBuffer sb = new StringBuffer("INSERT INTO personalities (id, name, tags, description) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.tags));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(");");
        return sb;
    }

}
