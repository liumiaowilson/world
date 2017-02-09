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
import org.wilson.world.model.EntityDef;

import com.mysql.jdbc.Statement;

public class EntityDefDAO extends AbstractDAO<EntityDef> {
    public static final String ITEM_TABLE_NAME = "entity_defs";
    
    private static final Logger logger = Logger.getLogger(EntityDefDAO.class);

    @Override
    public void create(EntityDef def) {
        if(def == null) {
            throw new DataException("EntityDef should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("EntityDef should have a valid name");
        }
        if(StringUtils.isBlank(def.def)) {
            throw new DataException("EntityDef should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into entity_defs(name, def) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, def.name);
            ps.setString(2, def.def);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                def.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create def", e);
            throw new DataException("failed to create def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(EntityDef def) {
        if(def == null) {
            throw new DataException("EntityDef should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("EntityDef should have a valid name");
        }
        if(StringUtils.isBlank(def.def)) {
            throw new DataException("EntityDef should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update entity_defs set name = ?, def = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, def.name);
            ps.setString(2, def.def);
            ps.setInt(3, def.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update def", e);
            throw new DataException("failed to update def");
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
            String sql = "delete from entity_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete def", e);
            throw new DataException("failed to delete def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public EntityDef get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from entity_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	EntityDef def = new EntityDef();
                def.id = id;
                def.name = rs.getString(2);
                def.def = rs.getString(3);
                return def;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get def", e);
            throw new DataException("failed to get def");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<EntityDef> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from entity_defs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<EntityDef> defs = new ArrayList<EntityDef>();
            while(rs.next()) {
            	EntityDef def = new EntityDef();
                def.id = rs.getInt(1);
                def.name = rs.getString(2);
                def.def = rs.getString(3);
                defs.add(def);
            }
            return defs;
        }
        catch(Exception e) {
            logger.error("failed to get defs", e);
            throw new DataException("failed to get defs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<EntityDef> query(QueryTemplate<EntityDef> template, Object... args) {
        return new ArrayList<EntityDef>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(EntityDef def) {
        return def.id;
    }

    @Override
    public StringBuffer exportSingle(EntityDef t) {
        StringBuffer sb = new StringBuffer("INSERT INTO entity_defs (id, name, def) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.def));
        sb.append(");");
        return sb;
    }

}
