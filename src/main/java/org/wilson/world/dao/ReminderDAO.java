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
import org.wilson.world.model.Reminder;

import com.mysql.jdbc.Statement;

public class ReminderDAO extends AbstractDAO<Reminder> {
    public static final String ITEM_TABLE_NAME = "reminders";
    
    private static final Logger logger = Logger.getLogger(ReminderDAO.class);

    @Override
    public void create(Reminder reminder) {
        if(reminder == null) {
            throw new DataException("Reminder should not be null");
        }
        if(StringUtils.isBlank(reminder.name)) {
            throw new DataException("Reminder should have a valid name");
        }
        if(StringUtils.isBlank(reminder.message)) {
            throw new DataException("Reminder should have a valid message");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into reminders(name, message, time, hours, minutes) values (?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reminder.name);
            ps.setString(2, reminder.message);
            ps.setLong(3, reminder.time);
            ps.setInt(4, reminder.hours);
            ps.setInt(5, reminder.minutes);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                reminder.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create reminder", e);
            throw new DataException("failed to create reminder");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Reminder reminder) {
        if(reminder == null) {
            throw new DataException("Reminder should not be null");
        }
        if(StringUtils.isBlank(reminder.name)) {
            throw new DataException("Reminder should have a valid name");
        }
        if(StringUtils.isBlank(reminder.message)) {
            throw new DataException("Reminder should have a valid message");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update reminders set name = ?, message = ?, time = ?, hours = ?, minutes = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, reminder.name);
            ps.setString(2, reminder.message);
            ps.setLong(3, reminder.time);
            ps.setInt(4, reminder.hours);
            ps.setInt(5, reminder.minutes);
            ps.setInt(6, reminder.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update reminder", e);
            throw new DataException("failed to update reminder");
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
            String sql = "delete from reminders where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete reminder", e);
            throw new DataException("failed to delete reminder");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Reminder get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from reminders where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Reminder reminder = new Reminder();
                reminder.id = id;
                reminder.name = rs.getString(2);
                reminder.message = rs.getString(3);
                reminder.time = rs.getLong(4);
                reminder.hours = rs.getInt(5);
                reminder.minutes = rs.getInt(6);
                return reminder;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get reminder", e);
            throw new DataException("failed to get reminder");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Reminder> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from reminders;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Reminder> reminders = new ArrayList<Reminder>();
            while(rs.next()) {
                Reminder reminder = new Reminder();
                reminder.id = rs.getInt(1);
                reminder.name = rs.getString(2);
                reminder.message = rs.getString(3);
                reminder.time = rs.getLong(4);
                reminder.hours = rs.getInt(5);
                reminder.minutes = rs.getInt(6);
                reminders.add(reminder);
            }
            return reminders;
        }
        catch(Exception e) {
            logger.error("failed to get reminders", e);
            throw new DataException("failed to get reminders");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Reminder> query(QueryTemplate<Reminder> template, Object... args) {
        return new ArrayList<Reminder>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Reminder reminder) {
        return reminder.id;
    }

    @Override
    public StringBuffer exportSingle(Reminder t) {
        StringBuffer sb = new StringBuffer("INSERT INTO reminders (id, name, message, time, hours, minutes) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.message));
        sb.append(",");
        sb.append(t.time);
        sb.append(",");
        sb.append(t.hours);
        sb.append(",");
        sb.append(t.minutes);
        sb.append(");");
        return sb;
    }

}
