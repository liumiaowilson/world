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
import org.wilson.world.model.Reaction;

import com.mysql.jdbc.Statement;

public class ReactionDAO extends AbstractDAO<Reaction> {
    public static final String ITEM_TABLE_NAME = "reactions";
    
    private static final Logger logger = Logger.getLogger(ReactionDAO.class);

    @Override
    public void create(Reaction reaction) {
        if(reaction == null) {
            throw new DataException("Reaction should not be null");
        }
        if(StringUtils.isBlank(reaction.name)) {
            throw new DataException("Reaction should have a valid name");
        }
        if(StringUtils.isBlank(reaction.condition)) {
            throw new DataException("Reaction should have a valid condition");
        }
        if(StringUtils.isBlank(reaction.result)) {
            throw new DataException("Reaction should have a valid result");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into reactions(name, cond, result) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reaction.name);
            ps.setString(2, reaction.condition);
            ps.setString(3, reaction.result);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                reaction.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create reaction", e);
            throw new DataException("failed to create reaction");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Reaction reaction) {
        if(reaction == null) {
            throw new DataException("Reaction should not be null");
        }
        if(StringUtils.isBlank(reaction.name)) {
            throw new DataException("Reaction should have a valid name");
        }
        if(StringUtils.isBlank(reaction.condition)) {
            throw new DataException("Reaction should have a valid condition");
        }
        if(StringUtils.isBlank(reaction.result)) {
            throw new DataException("Reaction should have a valid result");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update reactions set name = ?, cond = ?, result = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, reaction.name);
            ps.setString(2, reaction.condition);
            ps.setString(3, reaction.result);
            ps.setInt(4, reaction.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update reaction", e);
            throw new DataException("failed to update reaction");
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
            String sql = "delete from reactions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete reaction", e);
            throw new DataException("failed to delete reaction");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Reaction get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from reactions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Reaction reaction = new Reaction();
                reaction.id = id;
                reaction.name = rs.getString(2);
                reaction.condition = rs.getString(3);
                reaction.result = rs.getString(4);
                return reaction;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get reaction", e);
            throw new DataException("failed to get reaction");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Reaction> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from reactions;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Reaction> reactions = new ArrayList<Reaction>();
            while(rs.next()) {
                Reaction reaction = new Reaction();
                reaction.id = rs.getInt(1);
                reaction.name = rs.getString(2);
                reaction.condition = rs.getString(3);
                reaction.result = rs.getString(4);
                reactions.add(reaction);
            }
            return reactions;
        }
        catch(Exception e) {
            logger.error("failed to get reactions", e);
            throw new DataException("failed to get reactions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Reaction> query(QueryTemplate<Reaction> template, Object... args) {
        return new ArrayList<Reaction>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Reaction reaction) {
        return reaction.id;
    }

    @Override
    public StringBuffer exportSingle(Reaction t) {
        StringBuffer sb = new StringBuffer("INSERT INTO reactions (id, name, cond, result) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.condition));
        sb.append(",");
        sb.append(escapeStr(t.result));
        sb.append(");");
        return sb;
    }

}
