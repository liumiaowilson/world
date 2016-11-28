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
import org.wilson.world.model.Page;

import com.mysql.jdbc.Statement;

public class PageDAO extends AbstractDAO<Page> {
    public static final String ITEM_TABLE_NAME = "pages";
    
    private static final Logger logger = Logger.getLogger(PageDAO.class);

    @Override
    public void create(Page page) {
        if(page == null) {
            throw new DataException("Page should not be null");
        }
        if(StringUtils.isBlank(page.name)) {
            throw new DataException("Page should have a valid name");
        }
        if(StringUtils.isBlank(page.content)) {
            throw new DataException("Page should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into pages(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, page.name);
            ps.setString(2, page.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                page.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create page", e);
            throw new DataException("failed to create page");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Page page) {
        if(page == null) {
            throw new DataException("Page should not be null");
        }
        if(StringUtils.isBlank(page.name)) {
            throw new DataException("Page should have a valid name");
        }
        if(StringUtils.isBlank(page.content)) {
            throw new DataException("Page should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update pages set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, page.name);
            ps.setString(2, page.content);
            ps.setInt(3, page.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update page", e);
            throw new DataException("failed to update page");
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
            String sql = "delete from pages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete page", e);
            throw new DataException("failed to delete page");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Page get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from pages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Page page = new Page();
                page.id = id;
                page.name = rs.getString(2);
                page.content = rs.getString(3);
                return page;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get page", e);
            throw new DataException("failed to get page");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Page> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from pages;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Page> pages = new ArrayList<Page>();
            while(rs.next()) {
            	Page page = new Page();
                page.id = rs.getInt(1);
                page.name = rs.getString(2);
                page.content = rs.getString(3);
                pages.add(page);
            }
            return pages;
        }
        catch(Exception e) {
            logger.error("failed to get pages", e);
            throw new DataException("failed to get pages");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Page> query(QueryTemplate<Page> template, Object... args) {
        return new ArrayList<Page>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Page page) {
        return page.id;
    }

    @Override
    public StringBuffer exportSingle(Page t) {
        StringBuffer sb = new StringBuffer("INSERT INTO pages (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
