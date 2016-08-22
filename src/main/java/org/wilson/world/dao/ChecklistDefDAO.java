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
import org.wilson.world.model.ChecklistDef;

import com.mysql.jdbc.Statement;

public class ChecklistDefDAO extends AbstractDAO<ChecklistDef> {
    public static final String ITEM_TABLE_NAME = "checklist_defs";
    
    private static final Logger logger = Logger.getLogger(ChecklistDefDAO.class);

    @Override
    public void create(ChecklistDef def) {
        if(def == null) {
            throw new DataException("def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("def should have a valid name");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("def should have a valid description");
        }
        if(StringUtils.isBlank(def.content)) {
            throw new DataException("def should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into checklist_defs(name, description, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, def.name);
            ps.setString(2, def.description);
            ps.setString(3, def.content);
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
    public void update(ChecklistDef def) {
        if(def == null) {
            throw new DataException("def should not be null");
        }
        if(StringUtils.isBlank(def.name)) {
            throw new DataException("def should have a valid name");
        }
        if(StringUtils.isBlank(def.description)) {
            throw new DataException("def should have a valid description");
        }
        if(StringUtils.isBlank(def.content)) {
            throw new DataException("def should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update checklist_defs set name = ?, description = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, def.name);
            ps.setString(2, def.description);;
            ps.setString(3, def.content);
            ps.setInt(4, def.id);
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
            String sql = "delete from checklist_defs where id = ?;";
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
    public ChecklistDef get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from checklist_defs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ChecklistDef def = new ChecklistDef();
                def.id = id;
                def.name = rs.getString(2);
                def.description = rs.getString(3);
                def.content = rs.getString(4);
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
    public List<ChecklistDef> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from checklist_defs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ChecklistDef> defs = new ArrayList<ChecklistDef>();
            while(rs.next()) {
                ChecklistDef def = new ChecklistDef();
                def.id = rs.getInt(1);
                def.name = rs.getString(2);
                def.description = rs.getString(3);
                def.content = rs.getString(4);
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
    public List<ChecklistDef> query(QueryTemplate<ChecklistDef> template, Object... args) {
        return new ArrayList<ChecklistDef>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ChecklistDef def) {
        return def.id;
    }

    @Override
    public StringBuffer exportSingle(ChecklistDef t) {
        StringBuffer sb = new StringBuffer("INSERT INTO checklist_defs (id, name, description, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
