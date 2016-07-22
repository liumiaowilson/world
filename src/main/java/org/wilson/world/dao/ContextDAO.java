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
import org.wilson.world.model.Context;

import com.mysql.jdbc.Statement;

public class ContextDAO extends AbstractDAO<Context> {
    public static final String ITEM_TABLE_NAME = "contexts";
    
    private static final Logger logger = Logger.getLogger(ContextDAO.class);

    @Override
    public void create(Context context) {
        if(context == null) {
            throw new DataException("context should not be null");
        }
        if(StringUtils.isBlank(context.name)) {
            throw new DataException("context should have a valid name");
        }
        if(StringUtils.isBlank(context.color)) {
            throw new DataException("context should have a valid color");
        }
        if(StringUtils.isBlank(context.description)) {
            throw new DataException("context should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into contexts(name, color, description) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, context.name);
            ps.setString(2, context.color);
            ps.setString(3, context.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                context.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create context", e);
            throw new DataException("failed to create context");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Context context) {
        if(context == null) {
            throw new DataException("context should not be null");
        }
        if(StringUtils.isBlank(context.name)) {
            throw new DataException("context should have a valid name");
        }
        if(StringUtils.isBlank(context.color)) {
            throw new DataException("context should have a valid color");
        }
        if(StringUtils.isBlank(context.description)) {
            throw new DataException("context should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update contexts set name = ?, color = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, context.name);
            ps.setString(2, context.color);
            ps.setString(3, context.description);
            ps.setInt(4, context.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update context", e);
            throw new DataException("failed to update context");
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
            String sql = "delete from contexts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete context", e);
            throw new DataException("failed to delete context");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Context get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contexts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Context context = new Context();
                context.id = id;
                context.name = rs.getString(2);
                context.color = rs.getString(3);
                context.description = rs.getString(4);
                return context;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get context", e);
            throw new DataException("failed to get context");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Context> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from contexts;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Context> contexts = new ArrayList<Context>();
            while(rs.next()) {
                Context context = new Context();
                context.id = rs.getInt(1);
                context.name = rs.getString(2);
                context.color = rs.getString(3);
                context.description = rs.getString(4);
                contexts.add(context);
            }
            return contexts;
        }
        catch(Exception e) {
            logger.error("failed to get contexts", e);
            throw new DataException("failed to get contexts");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Context> query(QueryTemplate<Context> template, Object... args) {
        return new ArrayList<Context>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Context context) {
        return context.id;
    }

    @Override
    public StringBuffer exportSingle(Context t) {
        StringBuffer sb = new StringBuffer("INSERT INTO contexts (id, name, color, description) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(t.name);
        sb.append("','");
        sb.append(t.color);
        sb.append("','");
        sb.append(t.description);
        sb.append("');");
        return sb;
    }

}
