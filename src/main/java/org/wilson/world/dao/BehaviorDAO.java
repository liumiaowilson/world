package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Behavior;

import com.mysql.jdbc.Statement;

public class BehaviorDAO extends AbstractDAO<Behavior> {
    public static final String ITEM_TABLE_NAME = "behaviors";
    
    private static final Logger logger = Logger.getLogger(BehaviorDAO.class);

    @Override
    public void create(Behavior behavior) {
        if(behavior == null) {
            throw new DataException("behavior should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into behaviors(def_id, time) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, behavior.defId);
            ps.setLong(2, behavior.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                behavior.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create behavior", e);
            throw new DataException("failed to create behavior");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Behavior behavior) {
        if(behavior == null) {
            throw new DataException("behavior should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update behaviors set def_id = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, behavior.defId);
            ps.setLong(2, behavior.time);
            ps.setInt(3, behavior.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update behavior", e);
            throw new DataException("failed to update behavior");
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
            String sql = "delete from behaviors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete behavior", e);
            throw new DataException("failed to delete behavior");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Behavior get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from behaviors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Behavior behavior = new Behavior();
                behavior.id = id;
                behavior.defId = rs.getInt(2);
                behavior.time = rs.getLong(3);
                return behavior;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get behavior", e);
            throw new DataException("failed to get behavior");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Behavior> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from behaviors;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Behavior> behaviors = new ArrayList<Behavior>();
            while(rs.next()) {
                Behavior behavior = new Behavior();
                behavior.id = rs.getInt(1);
                behavior.defId = rs.getInt(2);
                behavior.time = rs.getLong(3);
                behaviors.add(behavior);
            }
            return behaviors;
        }
        catch(Exception e) {
            logger.error("failed to get behaviors", e);
            throw new DataException("failed to get behaviors");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Behavior> query(QueryTemplate<Behavior> template, Object... args) {
        return new ArrayList<Behavior>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Behavior behavior) {
        return behavior.id;
    }

    @Override
    public StringBuffer exportSingle(Behavior t) {
        StringBuffer sb = new StringBuffer("INSERT INTO behaviors (id, def_id, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.defId);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
