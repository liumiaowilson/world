package org.wilson.world.manager;

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

public class IdeaManager implements ItemTypeProvider {
    public static final String ITEM_TABLE_NAME = "ideas";
    
    private static final Logger logger = Logger.getLogger(IdeaManager.class);
    
    private static IdeaManager instance;
    
    private IdeaManager() {}
    
    public static IdeaManager getInstance() {
        if(instance == null) {
            instance = new IdeaManager();
        }
        return instance;
    }
    
    public void createIdea(Idea idea) {
        if(idea == null) {
            throw new DataException("idea should not be null");
        }
        if(StringUtils.isBlank(idea.name)) {
            throw new DataException("idea should have a valid name");
        }
        if(StringUtils.isBlank(idea.content)) {
            throw new DataException("idea should have a valid content");
        }
        
        Connection con = DBUtils.getConnection();
        try {
            String sql = "insert into ideas(name, content) values (?, ?);";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, idea.name);
            ps.setString(2, idea.content);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to create idea", e);
            throw new DataException("failed to create idea");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }
    
    public Idea getIdea(int id) {
        Connection con = DBUtils.getConnection();
        ResultSet rs = null;
        try {
            String sql = "select * from ideas where id = ?;";
            PreparedStatement ps = con.prepareStatement(sql);
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
            DBUtils.closeQuietly(con, rs);
        }
    }
    
    public List<Idea> getIdeas() {
        Connection con = DBUtils.getConnection();
        ResultSet rs = null;
        try {
            String sql = "select * from ideas;";
            PreparedStatement ps = con.prepareStatement(sql);
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
            DBUtils.closeQuietly(con, rs);
        }
    }
    
    public void updateIdea(Idea idea) {
        if(idea == null) {
            throw new DataException("idea should not be null");
        }
        if(StringUtils.isBlank(idea.name)) {
            throw new DataException("idea should have a valid name");
        }
        if(StringUtils.isBlank(idea.content)) {
            throw new DataException("idea should have a valid content");
        }
        
        Connection con = DBUtils.getConnection();
        try {
            String sql = "update ideas set name = ?, content = ? where id = ?;";
            PreparedStatement ps = con.prepareStatement(sql);
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
            DBUtils.closeQuietly(con, null);
        }
    }
    
    public void removeIdea(int id) {
        Connection con = DBUtils.getConnection();
        try {
            String sql = "delete from ideas where id = ?;";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to remove idea", e);
            throw new DataException("failed to remove idea");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }
}
