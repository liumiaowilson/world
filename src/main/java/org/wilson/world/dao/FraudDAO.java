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
import org.wilson.world.model.Fraud;

import com.mysql.jdbc.Statement;

public class FraudDAO extends AbstractDAO<Fraud> {
    public static final String ITEM_TABLE_NAME = "frauds";
    
    private static final Logger logger = Logger.getLogger(FraudDAO.class);

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public boolean isLoaded(Fraud t) {
        return t.content != null;
    }

    @Override
    public Fraud load(Fraud t) {
        return super.load(t);
    }

    @Override
    public Fraud unload(Fraud t) {
        t.content = null;
        return t;
    }

    @Override
    public void create(Fraud fraud) {
        if(fraud == null) {
            throw new DataException("Fraud should not be null");
        }
        if(StringUtils.isBlank(fraud.name)) {
            throw new DataException("Fraud should have a valid name");
        }
        if(StringUtils.isBlank(fraud.content)) {
            throw new DataException("Fraud should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into frauds(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fraud.name);
            ps.setString(2, fraud.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                fraud.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create fraud", e);
            throw new DataException("failed to create fraud");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Fraud fraud) {
        if(fraud == null) {
            throw new DataException("Fraud should not be null");
        }
        if(StringUtils.isBlank(fraud.name)) {
            throw new DataException("Fraud should have a valid name");
        }
        if(StringUtils.isBlank(fraud.content)) {
            throw new DataException("Fraud should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update frauds set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, fraud.name);
            ps.setString(2, fraud.content);
            ps.setInt(3, fraud.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update fraud", e);
            throw new DataException("failed to update fraud");
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
            String sql = "delete from frauds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete fraud", e);
            throw new DataException("failed to delete fraud");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Fraud get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from frauds where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Fraud fraud = new Fraud();
                fraud.id = id;
                fraud.name = rs.getString(2);
                if(!lazy) {
                    fraud.content = rs.getString(3);
                }
                return fraud;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get fraud", e);
            throw new DataException("failed to get fraud");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Fraud> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from frauds;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Fraud> frauds = new ArrayList<Fraud>();
            while(rs.next()) {
                Fraud fraud = new Fraud();
                fraud.id = rs.getInt(1);
                fraud.name = rs.getString(2);
                if(!lazy) {
                    fraud.content = rs.getString(3);
                }
                frauds.add(fraud);
            }
            return frauds;
        }
        catch(Exception e) {
            logger.error("failed to get frauds", e);
            throw new DataException("failed to get frauds");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Fraud> query(QueryTemplate<Fraud> template, Object... args) {
        return new ArrayList<Fraud>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Fraud fraud) {
        return fraud.id;
    }

    @Override
    public StringBuffer exportSingle(Fraud t) {
        StringBuffer sb = new StringBuffer("INSERT INTO frauds (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
