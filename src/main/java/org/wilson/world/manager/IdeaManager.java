package org.wilson.world.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Idea;

import com.mysql.jdbc.Statement;

public class IdeaManager implements ItemTypeProvider {
    public static final String NAME = "idea";
    
    public static final String ITEM_TABLE_NAME = "ideas";
    
    private static final Logger logger = Logger.getLogger(IdeaManager.class);
    
    private static IdeaManager instance;
    
    private Map<Integer, Idea> cache = new HashMap<Integer, Idea>();
    
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
        ResultSet rs = null;
        try {
            String sql = "insert into ideas(name, content) values (?, ?);";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, idea.name);
            ps.setString(2, idea.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                idea.id = id;
                cache.put(idea.id, idea);
            }
        }
        catch(Exception e) {
            logger.error("failed to create idea", e);
            throw new DataException("failed to create idea");
        }
        finally {
            DBUtils.closeQuietly(con, rs);
        }
    }
    
    public Idea getIdeaFromDB(int id) {
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
    
    public Idea getIdea(int id) {
        Idea idea = cache.get(id);
        if(idea != null) {
            return idea;
        }
        
        idea = getIdeaFromDB(id);
        if(idea != null) {
            cache.put(idea.id, idea);
            return idea;
        }
        else {
            return null;
        }
    }
    
    public List<Idea> getIdeasFromDB() {
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
    
    public List<Idea> getIdeas() {
        if(this.cache.isEmpty()) {
            List<Idea> ideas = getIdeasFromDB();
            for(Idea idea : ideas) {
                cache.put(idea.id, idea);
            }
        }
        List<Idea> result = new ArrayList<Idea>();
        for(Idea idea : cache.values()) {
            result.add(idea);
        }
        return result;
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
            
            cache.put(idea.id, idea);
        }
        catch(Exception e) {
            logger.error("failed to update idea", e);
            throw new DataException("failed to update idea");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }
    
    public void deleteIdea(int id) {
        Connection con = DBUtils.getConnection();
        try {
            String sql = "delete from ideas where id = ?;";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            
            cache.remove(id);
        }
        catch(Exception e) {
            logger.error("failed to delete idea", e);
            throw new DataException("failed to delete idea");
        }
        finally {
            DBUtils.closeQuietly(con, null);
        }
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Idea;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Idea idea = (Idea)target;
        return String.valueOf(idea.id);
    }
}
