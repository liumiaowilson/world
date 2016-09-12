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
import org.wilson.world.model.FlashCardSet;

import com.mysql.jdbc.Statement;

public class FlashCardSetDAO extends AbstractDAO<FlashCardSet> {
    public static final String ITEM_TABLE_NAME = "flashcard_sets";
    
    private static final Logger logger = Logger.getLogger(FlashCardSetDAO.class);

    @Override
    public void create(FlashCardSet set) {
        if(set == null) {
            throw new DataException("set should not be null");
        }
        if(StringUtils.isBlank(set.name)) {
            throw new DataException("set should have a valid name");
        }
        if(StringUtils.isBlank(set.description)) {
            throw new DataException("set should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into flashcard_sets(name, description) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, set.name);
            ps.setString(2, set.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                set.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create set", e);
            throw new DataException("failed to create set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(FlashCardSet set) {
        if(set == null) {
            throw new DataException("set should not be null");
        }
        if(StringUtils.isBlank(set.name)) {
            throw new DataException("set should have a valid name");
        }
        if(StringUtils.isBlank(set.description)) {
            throw new DataException("set should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update flashcard_sets set name = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, set.name);
            ps.setString(2, set.description);
            ps.setInt(3, set.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update set", e);
            throw new DataException("failed to update set");
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
            String sql = "delete from flashcard_sets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete set", e);
            throw new DataException("failed to delete set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public FlashCardSet get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from flashcard_sets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                FlashCardSet set = new FlashCardSet();
                set.id = id;
                set.name = rs.getString(2);
                set.description = rs.getString(3);
                return set;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get set", e);
            throw new DataException("failed to get set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<FlashCardSet> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from flashcard_sets;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<FlashCardSet> sets = new ArrayList<FlashCardSet>();
            while(rs.next()) {
                FlashCardSet set = new FlashCardSet();
                set.id = rs.getInt(1);
                set.name = rs.getString(2);
                set.description = rs.getString(3);
                sets.add(set);
            }
            return sets;
        }
        catch(Exception e) {
            logger.error("failed to get sets", e);
            throw new DataException("failed to get sets");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<FlashCardSet> query(QueryTemplate<FlashCardSet> template, Object... args) {
        return new ArrayList<FlashCardSet>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(FlashCardSet set) {
        return set.id;
    }

    @Override
    public StringBuffer exportSingle(FlashCardSet t) {
        StringBuffer sb = new StringBuffer("INSERT INTO flashcard_sets (id, name, description) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(");");
        return sb;
    }

}
