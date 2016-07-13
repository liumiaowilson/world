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
import org.wilson.world.model.Idea;

import com.mysql.jdbc.Statement;

public class IdeaDAO extends AbstractDAO<Idea> {
    public static final String ITEM_TABLE_NAME = "ideas";
    
    private static final Logger logger = Logger.getLogger(IdeaDAO.class);

    @Override
    public void create(Idea idea) {
        if(idea == null) {
            throw new DataException("idea should not be null");
        }
        if(StringUtils.isBlank(idea.name)) {
            throw new DataException("idea should have a valid name");
        }
        if(StringUtils.isBlank(idea.content)) {
            throw new DataException("idea should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into ideas(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, idea.name);
            ps.setString(2, idea.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                idea.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create idea", e);
            throw new DataException("failed to create idea");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Idea idea) {
        if(idea == null) {
            throw new DataException("idea should not be null");
        }
        if(StringUtils.isBlank(idea.name)) {
            throw new DataException("idea should have a valid name");
        }
        if(StringUtils.isBlank(idea.content)) {
            throw new DataException("idea should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update ideas set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, idea.name);
            ps.setString(2, idea.content);
            ps.setInt(3, idea.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update idea", e);
            throw new DataException("failed to update idea");
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
            String sql = "delete from ideas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete idea", e);
            throw new DataException("failed to delete idea");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Idea get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from ideas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Idea idea = new Idea();
                idea.id = id;
                idea.name = rs.getString(2);
                idea.content = rs.getString(3);
                return idea;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get idea", e);
            throw new DataException("failed to get idea");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Idea> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from ideas;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Idea> ideas = new ArrayList<Idea>();
            while(rs.next()) {
                Idea idea = new Idea();
                idea.id = rs.getInt(1);
                idea.name = rs.getString(2);
                idea.content = rs.getString(3);
                ideas.add(idea);
            }
            return ideas;
        }
        catch(Exception e) {
            logger.error("failed to get ideas", e);
            throw new DataException("failed to get ideas");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Idea> query(QueryTemplate template, Object... args) {
        return new ArrayList<Idea>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Idea idea) {
        return idea.id;
    }

}
