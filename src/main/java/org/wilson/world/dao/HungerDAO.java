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
import org.wilson.world.model.Hunger;

import com.mysql.jdbc.Statement;

public class HungerDAO extends AbstractDAO<Hunger> {
    public static final String ITEM_TABLE_NAME = "hungers";
    
    private static final Logger logger = Logger.getLogger(HungerDAO.class);

    @Override
    public void create(Hunger hunger) {
        if(hunger == null) {
            throw new DataException("Hunger should not be null");
        }
        if(StringUtils.isBlank(hunger.name)) {
            throw new DataException("Hunger should have a valid name");
        }
        if(StringUtils.isBlank(hunger.content)) {
            throw new DataException("Hunger should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into hungers(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hunger.name);
            ps.setString(2, hunger.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                hunger.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create hunger", e);
            throw new DataException("failed to create hunger");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Hunger hunger) {
        if(hunger == null) {
            throw new DataException("Hunger should not be null");
        }
        if(StringUtils.isBlank(hunger.name)) {
            throw new DataException("Hunger should have a valid name");
        }
        if(StringUtils.isBlank(hunger.content)) {
            throw new DataException("Hunger should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update hungers set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, hunger.name);
            ps.setString(2, hunger.content);
            ps.setInt(3, hunger.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update hunger", e);
            throw new DataException("failed to update hunger");
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
            String sql = "delete from hungers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete hunger", e);
            throw new DataException("failed to delete hunger");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Hunger get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hungers where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Hunger hunger = new Hunger();
                hunger.id = id;
                hunger.name = rs.getString(2);
                hunger.content = rs.getString(3);
                return hunger;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get hunger", e);
            throw new DataException("failed to get hunger");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Hunger> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from hungers;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Hunger> hungers = new ArrayList<Hunger>();
            while(rs.next()) {
            	Hunger hunger = new Hunger();
                hunger.id = rs.getInt(1);
                hunger.name = rs.getString(2);
                hunger.content = rs.getString(3);
                hungers.add(hunger);
            }
            return hungers;
        }
        catch(Exception e) {
            logger.error("failed to get hungers", e);
            throw new DataException("failed to get hungers");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Hunger> query(QueryTemplate<Hunger> template, Object... args) {
        return new ArrayList<Hunger>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Hunger hunger) {
        return hunger.id;
    }

    @Override
    public StringBuffer exportSingle(Hunger t) {
        StringBuffer sb = new StringBuffer("INSERT INTO hungers (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
