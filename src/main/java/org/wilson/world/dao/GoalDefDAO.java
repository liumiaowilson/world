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
import org.wilson.world.model.GoalDef;

import com.mysql.jdbc.Statement;

public class GoalDefDAO extends AbstractDAO<GoalDef> {
    public static final String ITEM_TABLE_NAME = "goal_defs";
    
    private static final Logger logger = Logger.getLogger(GoalDefDAO.class);

    @Override
    public void create(GoalDef def) {
        if(def == null) {
            throw new DataException("def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("def should have a valid name");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("def should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into goal_defs(name, description, steps, start_time, start_amount, end_time, end_amount) values (?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, def.name);
            ps.setString(2, def.description);
            ps.setInt(3, def.steps);
            ps.setLong(4, def.startTime);
            ps.setInt(5, def.startAmount);
            ps.setLong(6, def.endTime);
            ps.setInt(7, def.endAmount);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                def.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create def", e);
            throw new DataException("failed to create def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(GoalDef def) {
        if(def == null) {
            throw new DataException("def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("def should have a valid name");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("def should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update goal_defs set name = ?, description = ?, steps = ?, start_time = ?, start_amount = ?, end_time = ?, end_amount = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, def.name);
            ps.setString(2, def.description);
            ps.setInt(3, def.steps);
            ps.setLong(4, def.startTime);
            ps.setInt(5, def.startAmount);
            ps.setLong(6, def.endTime);
            ps.setInt(7, def.endAmount);
            ps.setInt(8, def.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update def", e);
            throw new DataException("failed to update def");
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
            String sql = "delete from goal_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete def", e);
            throw new DataException("failed to delete def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public GoalDef get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from goal_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                GoalDef def = new GoalDef();
                def.id = id;
                def.name = rs.getString(2);
                def.description = rs.getString(3);
                def.steps = rs.getInt(4);
                def.startTime = rs.getLong(5);
                def.startAmount = rs.getInt(6);
                def.endTime = rs.getLong(7);
                def.endAmount = rs.getInt(8);
                return def;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get def", e);
            throw new DataException("failed to get def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<GoalDef> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from goal_defs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<GoalDef> defs = new ArrayList<GoalDef>();
            while(rs.next()) {
                GoalDef def = new GoalDef();
                def.id = rs.getInt(1);
                def.name = rs.getString(2);
                def.description = rs.getString(3);
                def.steps = rs.getInt(4);
                def.startTime = rs.getLong(5);
                def.startAmount = rs.getInt(6);
                def.endTime = rs.getLong(7);
                def.endAmount = rs.getInt(8);
                defs.add(def);
            }
            return defs;
        }
        catch(Exception e) {
            logger.error("failed to get defs", e);
            throw new DataException("failed to get defs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<GoalDef> query(QueryTemplate<GoalDef> template, Object... args) {
        return new ArrayList<GoalDef>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(GoalDef def) {
        return def.id;
    }

    @Override
    public StringBuffer exportSingle(GoalDef t) {
        StringBuffer sb = new StringBuffer("INSERT INTO goal_defs (id, name, description, steps, start_time, start_amount, end_time, end_amount) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.description));
        sb.append("',");
        sb.append(t.steps);
        sb.append(",");
        sb.append(t.startTime);
        sb.append(",");
        sb.append(t.startAmount);
        sb.append(",");
        sb.append(t.endTime);
        sb.append(",");
        sb.append(t.endAmount);
        sb.append(");");
        return sb;
    }

}
