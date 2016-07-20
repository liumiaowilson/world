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
import org.wilson.world.model.Habit;

import com.mysql.jdbc.Statement;

public class HabitDAO extends AbstractDAO<Habit> {
    public static final String ITEM_TABLE_NAME = "habits";
    
    private static final Logger logger = Logger.getLogger(HabitDAO.class);

    @Override
    public void create(Habit habit) {
        if(habit == null) {
            throw new DataException("habit should not be null");
        }
        if(StringUtils.isBlank(habit.name)) {
            throw new DataException("habit should have a valid name");
        }
        if(StringUtils.isBlank(habit.description)) {
            throw new DataException("habit should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into habits(name, description, period) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, habit.name);
            ps.setString(2, habit.description);
            ps.setInt(3, habit.interval);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                habit.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create habit", e);
            throw new DataException("failed to create habit");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Habit habit) {
        if(habit == null) {
            throw new DataException("habit should not be null");
        }
        if(StringUtils.isBlank(habit.name)) {
            throw new DataException("habit should have a valid name");
        }
        if(StringUtils.isBlank(habit.description)) {
            throw new DataException("habit should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update habits set name = ?, description = ?, period = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, habit.name);
            ps.setString(2, habit.description);
            ps.setInt(3, habit.interval);
            ps.setInt(4, habit.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update habit", e);
            throw new DataException("failed to update habit");
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
            String sql = "delete from habits where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete habit", e);
            throw new DataException("failed to delete habit");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Habit get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from habits where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Habit habit = new Habit();
                habit.id = id;
                habit.name = rs.getString(2);
                habit.description = rs.getString(3);
                habit.interval = rs.getInt(4);
                return habit;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get habit", e);
            throw new DataException("failed to get habit");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Habit> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from habits;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Habit> habits = new ArrayList<Habit>();
            while(rs.next()) {
                Habit habit = new Habit();
                habit.id = rs.getInt(1);
                habit.name = rs.getString(2);
                habit.description = rs.getString(3);
                habit.interval = rs.getInt(4);
                habits.add(habit);
            }
            return habits;
        }
        catch(Exception e) {
            logger.error("failed to get habits", e);
            throw new DataException("failed to get habits");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Habit> query(QueryTemplate<Habit> template, Object... args) {
        return new ArrayList<Habit>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Habit habit) {
        return habit.id;
    }

}
