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
import org.wilson.world.model.Checklist;

import com.mysql.jdbc.Statement;

public class ChecklistDAO extends AbstractDAO<Checklist> {
    public static final String ITEM_TABLE_NAME = "checklists";
    
    private static final Logger logger = Logger.getLogger(ChecklistDAO.class);

    @Override
    public void create(Checklist checklist) {
        if(checklist == null) {
            throw new DataException("checklist should not be null");
        }
        if(StringUtils.isBlank(checklist.name)) {
            throw new DataException("checklist should have a valid name");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into checklists(def_id, name, progress) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, checklist.defId);
            ps.setString(2, checklist.name);
            ps.setString(3, checklist.progress);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                checklist.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create checklist", e);
            throw new DataException("failed to create checklist");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Checklist checklist) {
        if(checklist == null) {
            throw new DataException("checklist should not be null");
        }
        if(StringUtils.isBlank(checklist.name)) {
            throw new DataException("checklist should have a valid name");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update checklists set def_id = ?, name = ?, progress = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, checklist.defId);
            ps.setString(2, checklist.name);
            ps.setString(3, checklist.progress);
            ps.setInt(4, checklist.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update checklist", e);
            throw new DataException("failed to update checklist");
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
            String sql = "delete from checklists where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete checklist", e);
            throw new DataException("failed to delete checklist");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Checklist get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from checklists where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Checklist checklist = new Checklist();
                checklist.id = id;
                checklist.defId = rs.getInt(2);
                checklist.name = rs.getString(3);
                checklist.progress = rs.getString(4);
                return checklist;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get checklist", e);
            throw new DataException("failed to get checklist");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Checklist> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from checklists;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Checklist> checklists = new ArrayList<Checklist>();
            while(rs.next()) {
                Checklist checklist = new Checklist();
                checklist.id = rs.getInt(1);
                checklist.defId = rs.getInt(2);
                checklist.name = rs.getString(3);
                checklist.progress = rs.getString(4);
                checklists.add(checklist);
            }
            return checklists;
        }
        catch(Exception e) {
            logger.error("failed to get checklists", e);
            throw new DataException("failed to get checklists");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Checklist> query(QueryTemplate<Checklist> template, Object... args) {
        return new ArrayList<Checklist>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Checklist checklist) {
        return checklist.id;
    }

    @Override
    public StringBuffer exportSingle(Checklist t) {
        StringBuffer sb = new StringBuffer("INSERT INTO checklists (id, def_id, name, progress) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.defId);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.progress));
        sb.append(");");
        return sb;
    }

}
