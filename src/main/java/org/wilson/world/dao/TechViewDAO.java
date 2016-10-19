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
import org.wilson.world.model.TechView;

import com.mysql.jdbc.Statement;

public class TechViewDAO extends AbstractDAO<TechView> {
    public static final String ITEM_TABLE_NAME = "tech_views";
    
    private static final Logger logger = Logger.getLogger(TechViewDAO.class);

    @Override
    public void create(TechView view) {
        if(view == null) {
            throw new DataException("TechView should not be null");
        }
        if(StringUtils.isBlank(view.name)) {
            throw new DataException("TechView should have a valid name");
        }
        if(StringUtils.isBlank(view.content)) {
            throw new DataException("TechView should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into tech_views(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, view.name);
            ps.setString(2, view.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                view.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create tech view", e);
            throw new DataException("failed to create tech view");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TechView view) {
        if(view == null) {
            throw new DataException("TechView should not be null");
        }
        if(StringUtils.isBlank(view.name)) {
            throw new DataException("TechView should have a valid name");
        }
        if(StringUtils.isBlank(view.content)) {
            throw new DataException("TechView should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update tech_views set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, view.name);
            ps.setString(2, view.content);
            ps.setInt(3, view.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update tech view", e);
            throw new DataException("failed to update tech view");
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
            String sql = "delete from tech_views where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete tech view", e);
            throw new DataException("failed to delete tech view");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TechView get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tech_views where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TechView view = new TechView();
                view.id = id;
                view.name = rs.getString(2);
                view.content = rs.getString(3);
                return view;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get tech view", e);
            throw new DataException("failed to get tech view");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TechView> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tech_views;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TechView> views = new ArrayList<TechView>();
            while(rs.next()) {
                TechView view = new TechView();
                view.id = rs.getInt(1);
                view.name = rs.getString(2);
                view.content = rs.getString(3);
                views.add(view);
            }
            return views;
        }
        catch(Exception e) {
            logger.error("failed to get tech views", e);
            throw new DataException("failed to get tech views");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TechView> query(QueryTemplate<TechView> template, Object... args) {
        return new ArrayList<TechView>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TechView view) {
        return view.id;
    }

    @Override
    public StringBuffer exportSingle(TechView t) {
        StringBuffer sb = new StringBuffer("INSERT INTO tech_views (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
