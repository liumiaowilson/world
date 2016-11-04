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
import org.wilson.world.model.Bookmark;

import com.mysql.jdbc.Statement;

public class BookmarkDAO extends AbstractDAO<Bookmark> {
    public static final String ITEM_TABLE_NAME = "bookmarks";
    
    private static final Logger logger = Logger.getLogger(BookmarkDAO.class);

    @Override
    public void create(Bookmark bookmark) {
        if(bookmark == null) {
            throw new DataException("Bookmark should not be null");
        }
        if(StringUtils.isBlank(bookmark.name)) {
            throw new DataException("Bookmark should have a valid name");
        }
        if(StringUtils.isBlank(bookmark.group)) {
            throw new DataException("Bookmark should have a valid group");
        }
        if(StringUtils.isBlank(bookmark.content)) {
            throw new DataException("Bookmark should have a valid content");
        }
        if(StringUtils.isBlank(bookmark.url)) {
            throw new DataException("Bookmark should have a valid url");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into bookmarks(name, group_name, content, url) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bookmark.name);
            ps.setString(2, bookmark.group);
            ps.setString(3, bookmark.content);
            ps.setString(4, bookmark.url);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                bookmark.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create bookmark", e);
            throw new DataException("failed to create bookmark");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Bookmark bookmark) {
    	if(bookmark == null) {
            throw new DataException("Bookmark should not be null");
        }
        if(StringUtils.isBlank(bookmark.name)) {
            throw new DataException("Bookmark should have a valid name");
        }
        if(StringUtils.isBlank(bookmark.group)) {
            throw new DataException("Bookmark should have a valid group");
        }
        if(StringUtils.isBlank(bookmark.content)) {
            throw new DataException("Bookmark should have a valid content");
        }
        if(StringUtils.isBlank(bookmark.url)) {
            throw new DataException("Bookmark should have a valid url");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update bookmarks set name = ?, group_name = ?, content = ?, url = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, bookmark.name);
            ps.setString(2, bookmark.group);
            ps.setString(3, bookmark.content);
            ps.setString(4, bookmark.url);
            ps.setInt(5, bookmark.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update bookmark", e);
            throw new DataException("failed to update bookmark");
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
            String sql = "delete from bookmarks where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete bookmark", e);
            throw new DataException("failed to delete bookmark");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Bookmark get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from bookmarks where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Bookmark bookmark = new Bookmark();
                bookmark.id = id;
                bookmark.name = rs.getString(2);
                bookmark.group = rs.getString(3);
                bookmark.content = rs.getString(4);
                bookmark.url = rs.getString(5);
                return bookmark;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get bookmark", e);
            throw new DataException("failed to get bookmark");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Bookmark> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from bookmarks;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Bookmark> bookmarks = new ArrayList<Bookmark>();
            while(rs.next()) {
            	Bookmark bookmark = new Bookmark();
                bookmark.id = rs.getInt(1);
                bookmark.name = rs.getString(2);
                bookmark.group = rs.getString(3);
                bookmark.content = rs.getString(4);
                bookmark.url = rs.getString(5);
                bookmarks.add(bookmark);
            }
            return bookmarks;
        }
        catch(Exception e) {
            logger.error("failed to get bookmarks", e);
            throw new DataException("failed to get bookmarks");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Bookmark> query(QueryTemplate<Bookmark> template, Object... args) {
        return new ArrayList<Bookmark>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Bookmark bookmark) {
        return bookmark.id;
    }

    @Override
    public StringBuffer exportSingle(Bookmark t) {
        StringBuffer sb = new StringBuffer("INSERT INTO bookmarks (id, name, group_name, content, url) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.group));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(escapeStr(t.url));
        sb.append(");");
        return sb;
    }

}
