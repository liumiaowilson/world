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
import org.wilson.world.model.Proxy;

import com.mysql.jdbc.Statement;

public class ProxyDAO extends AbstractDAO<Proxy> {
    public static final String ITEM_TABLE_NAME = "proxies";
    
    private static final Logger logger = Logger.getLogger(ProxyDAO.class);

    @Override
    public void create(Proxy proxy) {
        if(proxy == null) {
            throw new DataException("Proxy should not be null");
        }
        if(StringUtils.isBlank(proxy.name)) {
            throw new DataException("Proxy should have a valid name");
        }
        if(StringUtils.isBlank(proxy.description)) {
            throw new DataException("Proxy should have a valid description");
        }
        if(StringUtils.isBlank(proxy.host)) {
            throw new DataException("Proxy should have a valid host");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into proxies(name, description, host, port) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, proxy.name);
            ps.setString(2, proxy.description);
            ps.setString(3, proxy.host);
            ps.setInt(4, proxy.port);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                proxy.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create proxy", e);
            throw new DataException("failed to create proxy");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Proxy proxy) {
        if(proxy == null) {
            throw new DataException("Proxy should not be null");
        }
        if(StringUtils.isBlank(proxy.name)) {
            throw new DataException("Proxy should have a valid name");
        }
        if(StringUtils.isBlank(proxy.description)) {
            throw new DataException("Proxy should have a valid description");
        }
        if(StringUtils.isBlank(proxy.host)) {
            throw new DataException("Proxy should have a valid host");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update proxies set name = ?, description = ?, host = ?, port = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, proxy.name);
            ps.setString(2, proxy.description);
            ps.setString(3, proxy.host);
            ps.setInt(4, proxy.port);
            ps.setInt(5, proxy.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update proxy", e);
            throw new DataException("failed to update proxy");
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
            String sql = "delete from proxies where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete proxy", e);
            throw new DataException("failed to delete proxy");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Proxy get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from proxies where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Proxy proxy = new Proxy();
                proxy.id = id;
                proxy.name = rs.getString(2);
                proxy.description = rs.getString(3);
                proxy.host = rs.getString(4);
                proxy.port = rs.getInt(5);
                return proxy;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get proxy", e);
            throw new DataException("failed to get proxy");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Proxy> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from proxies;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Proxy> proxies = new ArrayList<Proxy>();
            while(rs.next()) {
                Proxy proxy = new Proxy();
                proxy.id = rs.getInt(1);
                proxy.name = rs.getString(2);
                proxy.description = rs.getString(3);
                proxy.host = rs.getString(4);
                proxy.port = rs.getInt(5);
                proxies.add(proxy);
            }
            return proxies;
        }
        catch(Exception e) {
            logger.error("failed to get proxies", e);
            throw new DataException("failed to get proxies");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Proxy> query(QueryTemplate<Proxy> template, Object... args) {
        return new ArrayList<Proxy>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Proxy proxy) {
        return proxy.id;
    }

    @Override
    public StringBuffer exportSingle(Proxy t) {
        StringBuffer sb = new StringBuffer("INSERT INTO proxies (id, name, description, host, port) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.host));
        sb.append(",");
        sb.append(t.port);
        sb.append(");");
        return sb;
    }

}
