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
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Idea;

import com.mysql.jdbc.Statement;

public class IdeaManager implements ItemTypeProvider, CacheProvider {
    public static final String NAME = "idea";
    
    public static final String ITEM_TABLE_NAME = "ideas";
    
    private static final Logger logger = Logger.getLogger(IdeaManager.class);
    
    private static IdeaManager instance;
    
    private Map<Integer, Idea> cache = null;
    
    private IdeaManager() {
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static IdeaManager getInstance() {
        if(instance == null) {
            instance = new IdeaManager();
        }
        return instance;
    }
    
    private Map<Integer, Idea> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
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
                //do not load the cache on creating as creating may need to be fast for the first time
                if(this.cache != null) {
                    this.cache.put(idea.id, idea);
                }
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
    
    public Idea getIdeaFromDB(int id) {
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
    
    public Idea getIdea(int id) {
        Idea idea = getCache().get(id);
        if(idea != null) {
            return idea;
        }
        
        idea = getIdeaFromDB(id);
        if(idea != null) {
            getCache().put(idea.id, idea);
            return idea;
        }
        else {
            return null;
        }
    }
    
    public List<Idea> getIdeasFromDB() {
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
    
    public List<Idea> getIdeas() {
        List<Idea> result = new ArrayList<Idea>();
        for(Idea idea : getCache().values()) {
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
            
            getCache().put(idea.id, idea);
        }
        catch(Exception e) {
            logger.error("failed to update idea", e);
            throw new DataException("failed to update idea");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }
    
    public void deleteIdea(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "delete from ideas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            
            getCache().remove(id);
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

    @Override
    public String getCacheProviderName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public void reloadCache() {
        List<Idea> ideas = getIdeasFromDB();
        cache = new HashMap<Integer, Idea>();
        for(Idea idea : ideas) {
            cache.put(idea.id, idea);
        }
    }
}
