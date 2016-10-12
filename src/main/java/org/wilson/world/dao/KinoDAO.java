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
import org.wilson.world.model.Kino;

import com.mysql.jdbc.Statement;

public class KinoDAO extends AbstractDAO<Kino> {
    public static final String ITEM_TABLE_NAME = "kinos";
    
    private static final Logger logger = Logger.getLogger(KinoDAO.class);

    @Override
    public void create(Kino kino) {
        if(kino == null) {
            throw new DataException("Kino should not be null");
        }
        if(StringUtils.isBlank(kino.name)) {
            throw new DataException("Kino should have a valid name");
        }
        if(StringUtils.isBlank(kino.type)) {
            throw new DataException("Kino should have a valid type");
        }
        if(StringUtils.isBlank(kino.content)) {
            throw new DataException("Kino should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into kinos(name, type, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, kino.name);
            ps.setString(2, kino.type);
            ps.setString(3, kino.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                kino.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create kino", e);
            throw new DataException("failed to create kino");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Kino kino) {
        if(kino == null) {
            throw new DataException("Kino should not be null");
        }
        if(StringUtils.isBlank(kino.name)) {
            throw new DataException("Kino should have a valid name");
        }
        if(StringUtils.isBlank(kino.type)) {
            throw new DataException("Kino should have a valid type");
        }
        if(StringUtils.isBlank(kino.content)) {
            throw new DataException("Kino should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update kinos set name = ?, type = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, kino.name);
            ps.setString(2, kino.type);
            ps.setString(3, kino.content);
            ps.setInt(4, kino.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update kino", e);
            throw new DataException("failed to update kino");
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
            String sql = "delete from kinos where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete kino", e);
            throw new DataException("failed to delete kino");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Kino get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from kinos where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Kino kino = new Kino();
                kino.id = id;
                kino.name = rs.getString(2);
                kino.type = rs.getString(3);
                kino.content = rs.getString(4);
                return kino;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get kino", e);
            throw new DataException("failed to get kino");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Kino> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from kinos;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Kino> kinos = new ArrayList<Kino>();
            while(rs.next()) {
                Kino kino = new Kino();
                kino.id = rs.getInt(1);
                kino.name = rs.getString(2);
                kino.type = rs.getString(3);
                kino.content = rs.getString(4);
                kinos.add(kino);
            }
            return kinos;
        }
        catch(Exception e) {
            logger.error("failed to get kinos", e);
            throw new DataException("failed to get kinos");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Kino> query(QueryTemplate<Kino> template, Object... args) {
        return new ArrayList<Kino>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Kino kino) {
        return kino.id;
    }

    @Override
    public StringBuffer exportSingle(Kino t) {
        StringBuffer sb = new StringBuffer("INSERT INTO kinos (id, name, type, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
