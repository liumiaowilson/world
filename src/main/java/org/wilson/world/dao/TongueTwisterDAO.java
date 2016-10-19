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
import org.wilson.world.model.TongueTwister;

import com.mysql.jdbc.Statement;

public class TongueTwisterDAO extends AbstractDAO<TongueTwister> {
    public static final String ITEM_TABLE_NAME = "tongue_twisters";
    
    private static final Logger logger = Logger.getLogger(TongueTwisterDAO.class);

    @Override
    public void create(TongueTwister tt) {
        if(tt == null) {
            throw new DataException("TongueTwister should not be null");
        }
        if(StringUtils.isBlank(tt.name)) {
            throw new DataException("TongueTwister should have a valid name");
        }
        if(StringUtils.isBlank(tt.content)) {
            throw new DataException("TongueTwister should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into tongue_twisters(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tt.name);
            ps.setString(2, tt.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                tt.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create tongue twister", e);
            throw new DataException("failed to create tongue twister");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TongueTwister tt) {
        if(tt == null) {
            throw new DataException("TongueTwister should not be null");
        }
        if(StringUtils.isBlank(tt.name)) {
            throw new DataException("TongueTwister should have a valid name");
        }
        if(StringUtils.isBlank(tt.content)) {
            throw new DataException("TongueTwister should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update tongue_twisters set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, tt.name);
            ps.setString(2, tt.content);
            ps.setInt(3, tt.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update tongue twister", e);
            throw new DataException("failed to update tongue twister");
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
            String sql = "delete from tongue_twisters where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete tongue twister", e);
            throw new DataException("failed to delete tongue twister");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TongueTwister get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tongue_twisters where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TongueTwister tt = new TongueTwister();
                tt.id = id;
                tt.name = rs.getString(2);
                tt.content = rs.getString(3);
                return tt;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get tongue twister", e);
            throw new DataException("failed to get tongue twister");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TongueTwister> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from tongue_twisters;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TongueTwister> tts = new ArrayList<TongueTwister>();
            while(rs.next()) {
                TongueTwister tt = new TongueTwister();
                tt.id = rs.getInt(1);
                tt.name = rs.getString(2);
                tt.content = rs.getString(3);
                tts.add(tt);
            }
            return tts;
        }
        catch(Exception e) {
            logger.error("failed to get tongue twisters", e);
            throw new DataException("failed to get tongue twisters");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TongueTwister> query(QueryTemplate<TongueTwister> template, Object... args) {
        return new ArrayList<TongueTwister>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TongueTwister tt) {
        return tt.id;
    }

    @Override
    public StringBuffer exportSingle(TongueTwister t) {
        StringBuffer sb = new StringBuffer("INSERT INTO tongue_twisters (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
