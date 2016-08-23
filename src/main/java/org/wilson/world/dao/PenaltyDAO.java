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
import org.wilson.world.model.Penalty;

import com.mysql.jdbc.Statement;

public class PenaltyDAO extends AbstractDAO<Penalty> {
    public static final String ITEM_TABLE_NAME = "penalties";
    
    private static final Logger logger = Logger.getLogger(PenaltyDAO.class);

    @Override
    public void create(Penalty penalty) {
        if(penalty == null) {
            throw new DataException("Penalty should not be null");
        }
        if(StringUtils.isBlank(penalty.name)) {
            throw new DataException("Penalty should have a valid name");
        }
        if(StringUtils.isBlank(penalty.content)) {
            throw new DataException("Penalty should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into penalties(name, content, severity) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, penalty.name);
            ps.setString(2, penalty.content);
            ps.setInt(3, penalty.severity);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                penalty.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create penalty", e);
            throw new DataException("failed to create penalty");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Penalty penalty) {
        if(penalty == null) {
            throw new DataException("Penalty should not be null");
        }
        if(StringUtils.isBlank(penalty.name)) {
            throw new DataException("Penalty should have a valid name");
        }
        if(StringUtils.isBlank(penalty.content)) {
            throw new DataException("Penalty should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update penalties set name = ?, content = ?, severity = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, penalty.name);
            ps.setString(2, penalty.content);
            ps.setInt(3, penalty.severity);
            ps.setInt(4, penalty.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update penalty", e);
            throw new DataException("failed to update penalty");
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
            String sql = "delete from penalties where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete penalty", e);
            throw new DataException("failed to delete penalty");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Penalty get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from penalties where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Penalty penalty = new Penalty();
                penalty.id = id;
                penalty.name = rs.getString(2);
                penalty.content = rs.getString(3);
                penalty.severity = rs.getInt(4);
                return penalty;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get penalty", e);
            throw new DataException("failed to get penalty");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Penalty> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from penalties;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Penalty> penalties = new ArrayList<Penalty>();
            while(rs.next()) {
                Penalty penalty = new Penalty();
                penalty.id = rs.getInt(1);
                penalty.name = rs.getString(2);
                penalty.content = rs.getString(3);
                penalty.severity = rs.getInt(4);
                penalties.add(penalty);
            }
            return penalties;
        }
        catch(Exception e) {
            logger.error("failed to get penalties", e);
            throw new DataException("failed to get penalties");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Penalty> query(QueryTemplate<Penalty> template, Object... args) {
        return new ArrayList<Penalty>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Penalty penalty) {
        return penalty.id;
    }

    @Override
    public StringBuffer exportSingle(Penalty t) {
        StringBuffer sb = new StringBuffer("INSERT INTO penalties (id, name, content, severity) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(t.severity);
        sb.append(");");
        return sb;
    }

}
