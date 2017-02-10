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
import org.wilson.world.model.Pagelet;

import com.mysql.jdbc.Statement;

public class PageletDAO extends AbstractDAO<Pagelet> {
    public static final String ITEM_TABLE_NAME = "pagelets";
    
    private static final Logger logger = Logger.getLogger(PageletDAO.class);

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public boolean isLoaded(Pagelet t) {
        return t.serverCode != null || t.css != null || t.html != null || t.clientCode != null;
    }

    @Override
    public Pagelet load(Pagelet t) {
        return super.load(t);
    }

    @Override
    public Pagelet unload(Pagelet t) {
        t.serverCode = null;
        t.css = null;
        t.html = null;
        t.clientCode = null;
        return t;
    }
    
    @Override
    public void create(Pagelet pagelet) {
        if(pagelet == null) {
            throw new DataException("Pagelet should not be null");
        }
        if(StringUtils.isBlank(pagelet.name)) {
            throw new DataException("Pagelet should have a valid name");
        }
        if(StringUtils.isBlank(pagelet.title)) {
            throw new DataException("Pagelet should have a valid title");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into pagelets(name, title, target, server_code, css, html, client_code) values (?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pagelet.name);
            ps.setString(2, pagelet.title);
            ps.setString(3, pagelet.target);
            ps.setString(4, pagelet.serverCode);
            ps.setString(5, pagelet.css);
            ps.setString(6, pagelet.html);
            ps.setString(7, pagelet.clientCode);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                pagelet.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create pagelet", e);
            throw new DataException("failed to create pagelet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Pagelet pagelet) {
        if(pagelet == null) {
            throw new DataException("Pagelet should not be null");
        }
        if(StringUtils.isBlank(pagelet.name)) {
            throw new DataException("Pagelet should have a valid name");
        }
        if(StringUtils.isBlank(pagelet.title)) {
            throw new DataException("Pagelet should have a valid title");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update pagelets set name = ?, title = ?, target = ?, server_code = ?, css = ?, html = ?, client_code = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, pagelet.name);
            ps.setString(2, pagelet.title);
            ps.setString(3, pagelet.target);
            ps.setString(4, pagelet.serverCode);
            ps.setString(5, pagelet.css);
            ps.setString(6, pagelet.html);
            ps.setString(7, pagelet.clientCode);
            ps.setInt(8, pagelet.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update pagelet", e);
            throw new DataException("failed to update pagelet");
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
            String sql = "delete from pagelets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete pagelet", e);
            throw new DataException("failed to delete pagelet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Pagelet get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from pagelets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Pagelet pagelet = new Pagelet();
                pagelet.id = id;
                pagelet.name = rs.getString(2);
                pagelet.title = rs.getString(3);
                pagelet.target = rs.getString(4);
                if(!lazy) {
                	pagelet.serverCode = rs.getString(5);
                	pagelet.css = rs.getString(6);
                	pagelet.html = rs.getString(7);
                	pagelet.clientCode = rs.getString(8);
                }
                return pagelet;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get pagelet", e);
            throw new DataException("failed to get pagelet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Pagelet> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from pagelets;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Pagelet> pagelets = new ArrayList<Pagelet>();
            while(rs.next()) {
            	Pagelet pagelet = new Pagelet();
                pagelet.id = rs.getInt(1);
                pagelet.name = rs.getString(2);
                pagelet.title = rs.getString(3);
                pagelet.target = rs.getString(4);
                if(!lazy) {
                	pagelet.serverCode = rs.getString(5);
                	pagelet.css = rs.getString(6);
                	pagelet.html = rs.getString(7);
                	pagelet.clientCode = rs.getString(8);
                }
                pagelets.add(pagelet);
            }
            return pagelets;
        }
        catch(Exception e) {
            logger.error("failed to get pagelets", e);
            throw new DataException("failed to get pagelets");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Pagelet> query(QueryTemplate<Pagelet> template, Object... args) {
        return new ArrayList<Pagelet>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Pagelet pagelet) {
        return pagelet.id;
    }

    @Override
    public StringBuffer exportSingle(Pagelet t) {
        StringBuffer sb = new StringBuffer("INSERT INTO pagelets (id, name, title, target, server_code, css, html, client_code) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.title));
        sb.append(",");
        sb.append(escapeStr(t.target));
        sb.append(",");
        sb.append(escapeStr(t.serverCode));
        sb.append(",");
        sb.append(escapeStr(t.css));
        sb.append(",");
        sb.append(escapeStr(t.html));
        sb.append(",");
        sb.append(escapeStr(t.clientCode));
        sb.append(");");
        return sb;
    }

}
