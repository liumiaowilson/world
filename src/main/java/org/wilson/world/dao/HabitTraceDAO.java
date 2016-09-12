package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.HabitTrace;

import com.mysql.jdbc.Statement;

public class HabitTraceDAO extends AbstractDAO<HabitTrace> {
    public static final String ITEM_TABLE_NAME = "habit_traces";
    
    private static final Logger logger = Logger.getLogger(HabitTraceDAO.class);

    @Override
    public void create(HabitTrace trace) {
        if(trace == null) {
            throw new DataException("habit trace should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into habit_traces(habit_id, max_streak, streak, time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, trace.habitId);
            ps.setInt(2, trace.maxStreak);
            ps.setInt(3, trace.streak);
            ps.setLong(4, trace.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                trace.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create habit trace", e);
            throw new DataException("failed to create habit trace");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(HabitTrace trace) {
        if(trace == null) {
            throw new DataException("habit trace should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update habit_traces set habit_id = ?, max_streak = ?, streak = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, trace.habitId);
            ps.setInt(2, trace.maxStreak);
            ps.setInt(3, trace.streak);
            ps.setLong(4, trace.time);
            ps.setInt(5, trace.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update habit trace", e);
            throw new DataException("failed to update habit trace");
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
            String sql = "delete from habit_traces where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete habit trace", e);
            throw new DataException("failed to delete habit trace");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public HabitTrace get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from habit_traces where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                HabitTrace trace = new HabitTrace();
                trace.id = rs.getInt(1);
                trace.habitId = rs.getInt(2);
                trace.maxStreak = rs.getInt(3);
                trace.streak = rs.getInt(4);
                trace.time = rs.getLong(5);
                return trace;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get habit trace", e);
            throw new DataException("failed to get habit trace");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<HabitTrace> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from habit_traces;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<HabitTrace> traces = new ArrayList<HabitTrace>();
            while(rs.next()) {
                HabitTrace trace = new HabitTrace();
                trace.id = rs.getInt(1);
                trace.habitId = rs.getInt(2);
                trace.maxStreak = rs.getInt(3);
                trace.streak = rs.getInt(4);
                trace.time = rs.getLong(5);
                traces.add(trace);
            }
            return traces;
        }
        catch(Exception e) {
            logger.error("failed to get habit traces", e);
            throw new DataException("failed to get habit traces");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<HabitTrace> query(QueryTemplate<HabitTrace> template, Object... args) {
        return new ArrayList<HabitTrace>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(HabitTrace trace) {
        return trace.id;
    }

    @Override
    public StringBuffer exportSingle(HabitTrace t) {
        StringBuffer sb = new StringBuffer("INSERT INTO habit_traces (id, habit_id, max_streak, streak, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.habitId);
        sb.append(",");
        sb.append(t.maxStreak);
        sb.append(",");
        sb.append(t.streak);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
