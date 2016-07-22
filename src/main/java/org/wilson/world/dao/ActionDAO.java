package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Action;

import com.mysql.jdbc.Statement;

public class ActionDAO extends AbstractDAO<Action> {
    public static final String ITEM_TABLE_NAME = "actions";
    
    private static final Logger logger = Logger.getLogger(ActionDAO.class);
    
    public ActionDAO() {
        this.addQueryTemplate(new ActionQueryByNameTemplate());
    }

    @Override
    public void create(Action action) {
        if(action == null) {
            throw new DataException("action should not be null");
        }
        if(StringUtils.isBlank(action.name)) {
            throw new DataException("action should have a valid name");
        }
        if(StringUtils.isBlank(action.script)) {
            throw new DataException("action should have a valid script");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into actions(name, script) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, action.name);
            ps.setString(2, action.script);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                action.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create action", e);
            throw new DataException("failed to create action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Action action) {
        if(action == null) {
            throw new DataException("action should not be null");
        }
        if(StringUtils.isBlank(action.name)) {
            throw new DataException("action should have a valid name");
        }
        if(StringUtils.isBlank(action.script)) {
            throw new DataException("action should have a valid script");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update actions set name = ?, script = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, action.name);
            ps.setString(2, action.script);
            ps.setInt(3, action.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update action", e);
            throw new DataException("failed to update action");
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
            String sql = "delete from actions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete action", e);
            throw new DataException("failed to delete action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Action get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from actions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Action action = new Action();
                action.id = id;
                action.name = rs.getString(2);
                action.script = rs.getString(3);
                return action;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get action", e);
            throw new DataException("failed to get action");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Action> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from actions;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Action> actions = new ArrayList<Action>();
            while(rs.next()) {
                Action action = new Action();
                action.id = rs.getInt(1);
                action.name = rs.getString(2);
                action.script = rs.getString(3);
                actions.add(action);
            }
            return actions;
        }
        catch(Exception e) {
            logger.error("failed to get actions", e);
            throw new DataException("failed to get actions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Action> query(QueryTemplate<Action> template, Object... args) {
        if(template == null) {
            return new ArrayList<Action>();
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            ps = con.prepareStatement(template.getSQL());
            if(template.getQueryHelper() != null) {
                template.getQueryHelper().configurePreparedStatement(ps, args);
            }
            rs = ps.executeQuery();
            List<Action> actions = new ArrayList<Action>();
            while(rs.next()) {
                Action action = new Action();
                action.id = rs.getInt(1);
                action.name = rs.getString(2);
                action.script = rs.getString(3);
                actions.add(action);
            }
            return actions;
        }
        catch(Exception e) {
            logger.error("failed to query actions", e);
            throw new DataException("failed to query actions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }
    

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }
    

    @Override
    public int getId(Action action) {
        return action.id;
    }
    
    public static class ActionQueryByNameTemplate implements QueryTemplate<Action> {
        public static final String NAME = "action_query_by_name";
        
        private QueryHelper helper = new QueryHelper() {

            @Override
            public void configurePreparedStatement(PreparedStatement ps, Object... args) throws SQLException {
                String name = (String) args[0];
                ps.setString(1, name);
            }
            
        };
        
        @Override
        public String getID() {
            return NAME;
        }

        @Override
        public String getSQL() {
            return "select * from actions where name = ?;";
        }

        @Override
        public QueryHelper getQueryHelper() {
            return helper;
        }

        @Override
        public boolean accept(Action t, Object... args) {
            String name = (String) args[0];
            return t.name.equals(name);
        }
    }

    @Override
    public StringBuffer exportSingle(Action t) {
        StringBuffer sb = new StringBuffer("");
        sb.append("INSERT INTO actions(id, name, script) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(t.name);
        sb.append("','");
        sb.append(t.script);
        sb.append("');");
        return sb;
    }
}
