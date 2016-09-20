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
import org.wilson.world.model.Period;

import com.mysql.jdbc.Statement;

public class PeriodDAO extends AbstractDAO<Period> {
    public static final String ITEM_TABLE_NAME = "periods";
    
    private static final Logger logger = Logger.getLogger(PeriodDAO.class);

    @Override
    public void create(Period period) {
        if(period == null) {
            throw new DataException("period should not be null");
        }
        if(StringUtils.isBlank(period.status)) {
            throw new DataException("period should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into periods(status, time) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, period.status);
            ps.setLong(2, period.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                period.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create period", e);
            throw new DataException("failed to create period");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Period period) {
        if(period == null) {
            throw new DataException("period should not be null");
        }
        if(StringUtils.isBlank(period.status)) {
            throw new DataException("period should have a valid status");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update periods set status = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, period.status);
            ps.setLong(2, period.time);
            ps.setInt(3, period.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update period", e);
            throw new DataException("failed to update period");
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
            String sql = "delete from periods where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete period", e);
            throw new DataException("failed to delete period");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Period get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from periods where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Period period = new Period();
                period.id = id;
                period.status = rs.getString(2);
                period.time = rs.getLong(3);
                return period;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get period", e);
            throw new DataException("failed to get period");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Period> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from periods;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Period> periods = new ArrayList<Period>();
            while(rs.next()) {
                Period period = new Period();
                period.id = rs.getInt(1);
                period.status = rs.getString(2);
                period.time = rs.getLong(3);
                periods.add(period);
            }
            return periods;
        }
        catch(Exception e) {
            logger.error("failed to get periods", e);
            throw new DataException("failed to get periods");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Period> query(QueryTemplate<Period> template, Object... args) {
        return new ArrayList<Period>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Period period) {
        return period.id;
    }

    @Override
    public StringBuffer exportSingle(Period t) {
        StringBuffer sb = new StringBuffer("INSERT INTO periods (id, status, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.status));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
