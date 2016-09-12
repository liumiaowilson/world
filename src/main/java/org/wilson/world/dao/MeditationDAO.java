package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Meditation;

import com.mysql.jdbc.Statement;

public class MeditationDAO extends AbstractDAO<Meditation> {
    public static final String ITEM_TABLE_NAME = "meditations";
    
    private static final Logger logger = Logger.getLogger(MeditationDAO.class);

    @Override
    public void create(Meditation meditation) {
        if(meditation == null) {
            throw new DataException("meditation should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into meditations(time, duration) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, meditation.time);
            ps.setLong(2, meditation.duration);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                meditation.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create meditation", e);
            throw new DataException("failed to create meditation");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Meditation meditation) {
        if(meditation == null) {
            throw new DataException("Meditation should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update meditations set time = ?, duration = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setLong(1, meditation.time);
            ps.setLong(2, meditation.duration);
            ps.setInt(3, meditation.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update meditation", e);
            throw new DataException("failed to update meditation");
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
            String sql = "delete from meditations where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete meditation", e);
            throw new DataException("failed to delete meditation");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Meditation get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from meditations where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Meditation meditation = new Meditation();
                meditation.id = id;
                meditation.time = rs.getLong(2);
                meditation.duration = rs.getLong(3);
                return meditation;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get meditation", e);
            throw new DataException("failed to get meditation");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Meditation> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from meditations;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Meditation> meditations = new ArrayList<Meditation>();
            while(rs.next()) {
                Meditation meditation = new Meditation();
                meditation.id = rs.getInt(1);
                meditation.time = rs.getLong(2);
                meditation.duration = rs.getLong(3);
                meditations.add(meditation);
            }
            return meditations;
        }
        catch(Exception e) {
            logger.error("failed to get meditations", e);
            throw new DataException("failed to get meditations");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Meditation> query(QueryTemplate<Meditation> template, Object... args) {
        return new ArrayList<Meditation>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Meditation meditation) {
        return meditation.id;
    }

    @Override
    public StringBuffer exportSingle(Meditation t) {
        StringBuffer sb = new StringBuffer("INSERT INTO meditations (id, time, duration) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.time);
        sb.append(",");
        sb.append(t.duration);
        sb.append(");");
        return sb;
    }

}
