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
import org.wilson.world.model.RomanceFactor;

import com.mysql.jdbc.Statement;

public class RomanceFactorDAO extends AbstractDAO<RomanceFactor> {
    public static final String ITEM_TABLE_NAME = "romance_factors";
    
    private static final Logger logger = Logger.getLogger(RomanceFactorDAO.class);

    @Override
    public void create(RomanceFactor factor) {
        if(factor == null) {
            throw new DataException("factor should not be null");
        }
        if(StringUtils.isBlank(factor.name)) {
            throw new DataException("factor should have a valid name");
        }
        if(StringUtils.isBlank(factor.content)) {
            throw new DataException("factor should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into romance_factors(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, factor.name);
            ps.setString(2, factor.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                factor.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create factor", e);
            throw new DataException("failed to create factor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(RomanceFactor factor) {
        if(factor == null) {
            throw new DataException("factor should not be null");
        }
        if(StringUtils.isBlank(factor.name)) {
            throw new DataException("factor should have a valid name");
        }
        if(StringUtils.isBlank(factor.content)) {
            throw new DataException("factor should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update romance_factors set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, factor.name);
            ps.setString(2, factor.content);
            ps.setInt(3, factor.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update factor", e);
            throw new DataException("failed to update factor");
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
            String sql = "delete from romance_factors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete factor", e);
            throw new DataException("failed to delete factor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public RomanceFactor get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from romance_factors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                RomanceFactor factor = new RomanceFactor();
                factor.id = id;
                factor.name = rs.getString(2);
                factor.content = rs.getString(3);
                return factor;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get factor", e);
            throw new DataException("failed to get factor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RomanceFactor> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from romance_factors;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<RomanceFactor> factors = new ArrayList<RomanceFactor>();
            while(rs.next()) {
                RomanceFactor factor = new RomanceFactor();
                factor.id = rs.getInt(1);
                factor.name = rs.getString(2);
                factor.content = rs.getString(3);
                factors.add(factor);
            }
            return factors;
        }
        catch(Exception e) {
            logger.error("failed to get factors", e);
            throw new DataException("failed to get factors");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RomanceFactor> query(QueryTemplate<RomanceFactor> template, Object... args) {
        return new ArrayList<RomanceFactor>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(RomanceFactor factor) {
        return factor.id;
    }

    @Override
    public StringBuffer exportSingle(RomanceFactor t) {
        StringBuffer sb = new StringBuffer("INSERT INTO romance_factors (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
