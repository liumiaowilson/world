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
import org.wilson.world.model.Feed;

import com.mysql.jdbc.Statement;

public class FeedDAO extends AbstractDAO<Feed> {
    public static final String ITEM_TABLE_NAME = "feeds";
    
    private static final Logger logger = Logger.getLogger(FeedDAO.class);

    @Override
    public void create(Feed feed) {
        if(feed == null) {
            throw new DataException("feed should not be null");
        }
        if(StringUtils.isBlank(feed.name)) {
            throw new DataException("feed should have a valid name");
        }
        if(StringUtils.isBlank(feed.description)) {
            throw new DataException("feed should have a valid description");
        }
        if(StringUtils.isBlank(feed.rss)) {
            throw new DataException("feed should have a valid rss");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into feeds(name, description, rss) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, feed.name);
            ps.setString(2, feed.description);
            ps.setString(3, feed.rss);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                feed.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create feed", e);
            throw new DataException("failed to create feed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Feed feed) {
        if(feed == null) {
            throw new DataException("feed should not be null");
        }
        if(StringUtils.isBlank(feed.name)) {
            throw new DataException("feed should have a valid name");
        }
        if(StringUtils.isBlank(feed.description)) {
            throw new DataException("feed should have a valid description");
        }
        if(StringUtils.isBlank(feed.rss)) {
            throw new DataException("feed should have a valid rss");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update feeds set name = ?, description = ?, rss = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, feed.name);
            ps.setString(2, feed.description);
            ps.setString(3, feed.rss);
            ps.setInt(4, feed.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update feed", e);
            throw new DataException("failed to update feed");
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
            String sql = "delete from feeds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete feed", e);
            throw new DataException("failed to delete feed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Feed get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from feeds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Feed feed = new Feed();
                feed.id = id;
                feed.name = rs.getString(2);
                feed.description = rs.getString(3);
                feed.rss = rs.getString(4);
                return feed;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get feed", e);
            throw new DataException("failed to get feed");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Feed> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from feeds;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Feed> feeds = new ArrayList<Feed>();
            while(rs.next()) {
                Feed feed = new Feed();
                feed.id = rs.getInt(1);
                feed.name = rs.getString(2);
                feed.description = rs.getString(3);
                feed.rss = rs.getString(4);
                feeds.add(feed);
            }
            return feeds;
        }
        catch(Exception e) {
            logger.error("failed to get feeds", e);
            throw new DataException("failed to get feeds");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Feed> query(QueryTemplate<Feed> template, Object... args) {
        return new ArrayList<Feed>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Feed feed) {
        return feed.id;
    }

    @Override
    public StringBuffer exportSingle(Feed t) {
        StringBuffer sb = new StringBuffer("INSERT INTO feeds (id, name, description, rss) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.rss));
        sb.append(");");
        return sb;
    }

}
