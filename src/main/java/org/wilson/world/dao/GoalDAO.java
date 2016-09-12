package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Goal;

import com.mysql.jdbc.Statement;

public class GoalDAO extends AbstractDAO<Goal> {
    public static final String ITEM_TABLE_NAME = "goals";
    
    private static final Logger logger = Logger.getLogger(GoalDAO.class);

    @Override
    public void create(Goal goal) {
        if(goal == null) {
            throw new DataException("goal should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into goals(def_id, time, amount) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, goal.defId);
            ps.setLong(2, goal.time);
            ps.setInt(3, goal.amount);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                goal.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create goal", e);
            throw new DataException("failed to create goal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Goal goal) {
        if(goal == null) {
            throw new DataException("goal should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update goals set def_id = ?, time = ?, amount = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, goal.defId);
            ps.setLong(2, goal.time);
            ps.setInt(3, goal.amount);;
            ps.setInt(4, goal.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update goal", e);
            throw new DataException("failed to update goal");
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
            String sql = "delete from goals where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete goal", e);
            throw new DataException("failed to delete goal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Goal get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from goals where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Goal goal = new Goal();
                goal.id = id;
                goal.defId = rs.getInt(2);
                goal.time = rs.getLong(3);
                goal.amount = rs.getInt(4);
                return goal;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get goal", e);
            throw new DataException("failed to get goal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Goal> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from goals;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Goal> goals = new ArrayList<Goal>();
            while(rs.next()) {
                Goal goal = new Goal();
                goal.id = rs.getInt(1);
                goal.defId = rs.getInt(2);
                goal.time = rs.getLong(3);
                goal.amount = rs.getInt(4);
                goals.add(goal);
            }
            return goals;
        }
        catch(Exception e) {
            logger.error("failed to get goals", e);
            throw new DataException("failed to get goals");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Goal> query(QueryTemplate<Goal> template, Object... args) {
        return new ArrayList<Goal>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Goal goal) {
        return goal.id;
    }

    @Override
    public StringBuffer exportSingle(Goal t) {
        StringBuffer sb = new StringBuffer("INSERT INTO goals (id, def_id, time, amount) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.defId);
        sb.append(",");
        sb.append(t.time);
        sb.append(",");
        sb.append(t.amount);
        sb.append(");");
        return sb;
    }

}
