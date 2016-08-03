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
import org.wilson.world.model.Detail;

import com.mysql.jdbc.Statement;

public class DetailDAO extends AbstractDAO<Detail> {
    public static final String ITEM_TABLE_NAME = "details";
    
    private static final Logger logger = Logger.getLogger(DetailDAO.class);

    @Override
    public void create(Detail detail) {
        if(detail == null) {
            throw new DataException("detail should not be null");
        }
        if(StringUtils.isBlank(detail.name)) {
            throw new DataException("detail should have a valid name");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("detail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into details(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, detail.name);
            ps.setString(2, detail.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                detail.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create detail", e);
            throw new DataException("failed to create detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Detail detail) {
        if(detail == null) {
            throw new DataException("detail should not be null");
        }
        if(StringUtils.isBlank(detail.name)) {
            throw new DataException("detail should have a valid name");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("detail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update details set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, detail.name);
            ps.setString(2, detail.content);
            ps.setInt(3, detail.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update detail", e);
            throw new DataException("failed to update detail");
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
            String sql = "delete from details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete detail", e);
            throw new DataException("failed to delete detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Detail get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Detail detail = new Detail();
                detail.id = id;
                detail.name = rs.getString(2);
                detail.content = rs.getString(3);
                return detail;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get detail", e);
            throw new DataException("failed to get detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Detail> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from details;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Detail> details = new ArrayList<Detail>();
            while(rs.next()) {
                Detail detail = new Detail();
                detail.id = rs.getInt(1);
                detail.name = rs.getString(2);
                detail.content = rs.getString(3);
                details.add(detail);
            }
            return details;
        }
        catch(Exception e) {
            logger.error("failed to get details", e);
            throw new DataException("failed to get details");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Detail> query(QueryTemplate<Detail> template, Object... args) {
        return new ArrayList<Detail>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Detail detail) {
        return detail.id;
    }

    @Override
    public StringBuffer exportSingle(Detail t) {
        StringBuffer sb = new StringBuffer("INSERT INTO details (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.content));
        sb.append("');");
        return sb;
    }

}
