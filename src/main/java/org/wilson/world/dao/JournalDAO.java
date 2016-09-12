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
import org.wilson.world.model.Journal;

import com.mysql.jdbc.Statement;

public class JournalDAO extends AbstractDAO<Journal> {
    public static final String ITEM_TABLE_NAME = "journals";
    
    private static final Logger logger = Logger.getLogger(JournalDAO.class);

    @Override
    public void create(Journal journal) {
        if(journal == null) {
            throw new DataException("journal should not be null");
        }
        if(StringUtils.isBlank(journal.name)) {
            throw new DataException("journal should have a valid name");
        }
        if(StringUtils.isBlank(journal.weather)) {
            throw new DataException("journal should have a valid weather");
        }
        if(StringUtils.isBlank(journal.content)) {
            throw new DataException("journal should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into journals(name, weather, content, time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, journal.name);
            ps.setString(2, journal.weather);
            ps.setString(3, journal.content);
            ps.setLong(4, journal.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                journal.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create journal", e);
            throw new DataException("failed to create journal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Journal journal) {
        if(journal == null) {
            throw new DataException("journal should not be null");
        }
        if(StringUtils.isBlank(journal.name)) {
            throw new DataException("journal should have a valid name");
        }
        if(StringUtils.isBlank(journal.weather)) {
            throw new DataException("journal should have a valid weather");
        }
        if(StringUtils.isBlank(journal.content)) {
            throw new DataException("journal should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update journals set name = ?, weather = ?, content = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, journal.name);
            ps.setString(2, journal.weather);;
            ps.setString(3, journal.content);
            ps.setLong(4, journal.time);;
            ps.setInt(5, journal.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update journal", e);
            throw new DataException("failed to update journal");
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
            String sql = "delete from journals where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete journal", e);
            throw new DataException("failed to delete journal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Journal get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from journals where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Journal journal = new Journal();
                journal.id = id;
                journal.name = rs.getString(2);
                journal.weather = rs.getString(3);
                journal.content = rs.getString(4);
                journal.time = rs.getLong(5);
                return journal;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get journal", e);
            throw new DataException("failed to get journal");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Journal> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from journals;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Journal> journals = new ArrayList<Journal>();
            while(rs.next()) {
                Journal journal = new Journal();
                journal.id = rs.getInt(1);
                journal.name = rs.getString(2);
                journal.weather = rs.getString(3);
                journal.content = rs.getString(4);
                journal.time = rs.getLong(5);
                journals.add(journal);
            }
            return journals;
        }
        catch(Exception e) {
            logger.error("failed to get journals", e);
            throw new DataException("failed to get journals");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Journal> query(QueryTemplate<Journal> template, Object... args) {
        return new ArrayList<Journal>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Journal journal) {
        return journal.id;
    }

    @Override
    public StringBuffer exportSingle(Journal t) {
        StringBuffer sb = new StringBuffer("INSERT INTO journals (id, name, weather, content, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.weather));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
