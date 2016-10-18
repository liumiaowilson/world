package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Sign;

import com.mysql.jdbc.Statement;

public class SignDAO extends AbstractDAO<Sign> {
    public static final String ITEM_TABLE_NAME = "signs";
    
    private static final Logger logger = Logger.getLogger(SignDAO.class);

    @Override
    public void create(Sign sign) {
        if(sign == null) {
            throw new DataException("Sign should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into signs(card_id, step, time) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, sign.cardId);
            ps.setInt(2, sign.step);
            ps.setLong(3, sign.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                sign.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create sign", e);
            throw new DataException("failed to create sign");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Sign sign) {
        if(sign == null) {
            throw new DataException("Sign should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update signs set card_id = ?, step = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, sign.cardId);
            ps.setInt(2, sign.step);
            ps.setLong(3, sign.time);
            ps.setInt(4, sign.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update sign", e);
            throw new DataException("failed to update sign");
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
            String sql = "delete from signs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete sign", e);
            throw new DataException("failed to delete sign");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Sign get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from signs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Sign sign = new Sign();
                sign.id = id;
                sign.cardId = rs.getInt(2);
                sign.step = rs.getInt(3);
                sign.time = rs.getLong(4);
                return sign;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get sign", e);
            throw new DataException("failed to get sign");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Sign> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from signs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Sign> signs = new ArrayList<Sign>();
            while(rs.next()) {
                Sign sign = new Sign();
                sign.id = rs.getInt(1);
                sign.cardId = rs.getInt(2);
                sign.step = rs.getInt(3);
                sign.time = rs.getLong(4);
                signs.add(sign);
            }
            return signs;
        }
        catch(Exception e) {
            logger.error("failed to get signs", e);
            throw new DataException("failed to get signs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Sign> query(QueryTemplate<Sign> template, Object... args) {
        return new ArrayList<Sign>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Sign sign) {
        return sign.id;
    }

    @Override
    public StringBuffer exportSingle(Sign t) {
        StringBuffer sb = new StringBuffer("INSERT INTO signs (id, card_id, step, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.cardId);
        sb.append(",");
        sb.append(t.step);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
