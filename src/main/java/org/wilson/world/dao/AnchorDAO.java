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
import org.wilson.world.model.Anchor;

import com.mysql.jdbc.Statement;

public class AnchorDAO extends AbstractDAO<Anchor> {
    public static final String ITEM_TABLE_NAME = "anchors";
    
    private static final Logger logger = Logger.getLogger(AnchorDAO.class);

    @Override
    public void create(Anchor anchor) {
        if(anchor == null) {
            throw new DataException("Anchor should not be null");
        }
        if(StringUtils.isBlank(anchor.name)) {
            throw new DataException("Anchor should have a valid name");
        }
        if(StringUtils.isBlank(anchor.type)) {
            throw new DataException("Anchor should have a valid type");
        }
        if(StringUtils.isBlank(anchor.stimuli)) {
            throw new DataException("Anchor should have a valid stimuli");
        }
        if(StringUtils.isBlank(anchor.response)) {
            throw new DataException("Anchor should have a valid response");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into anchors(name, type, stimuli, response) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, anchor.name);
            ps.setString(2, anchor.type);
            ps.setString(3, anchor.stimuli);
            ps.setString(4, anchor.response);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                anchor.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create anchor", e);
            throw new DataException("failed to create anchor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Anchor anchor) {
        if(anchor == null) {
            throw new DataException("Anchor should not be null");
        }
        if(StringUtils.isBlank(anchor.name)) {
            throw new DataException("Anchor should have a valid name");
        }
        if(StringUtils.isBlank(anchor.type)) {
            throw new DataException("Anchor should have a valid type");
        }
        if(StringUtils.isBlank(anchor.stimuli)) {
            throw new DataException("Anchor should have a valid stimuli");
        }
        if(StringUtils.isBlank(anchor.response)) {
            throw new DataException("Anchor should have a valid response");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update anchors set name = ?, type = ?, stimuli = ?, response = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, anchor.name);
            ps.setString(2, anchor.type);
            ps.setString(3, anchor.stimuli);
            ps.setString(4, anchor.response);
            ps.setInt(5, anchor.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update anchor", e);
            throw new DataException("failed to update anchor");
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
            String sql = "delete from anchors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete anchor", e);
            throw new DataException("failed to delete anchor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Anchor get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from anchors where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Anchor anchor = new Anchor();
                anchor.id = id;
                anchor.name = rs.getString(2);
                anchor.type = rs.getString(3);
                anchor.stimuli = rs.getString(4);
                anchor.response = rs.getString(5);
                return anchor;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get anchor", e);
            throw new DataException("failed to get anchor");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Anchor> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from anchors;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Anchor> anchors = new ArrayList<Anchor>();
            while(rs.next()) {
                Anchor anchor = new Anchor();
                anchor.id = rs.getInt(1);
                anchor.name = rs.getString(2);
                anchor.type = rs.getString(3);
                anchor.stimuli = rs.getString(4);
                anchor.response = rs.getString(5);
                anchors.add(anchor);
            }
            return anchors;
        }
        catch(Exception e) {
            logger.error("failed to get anchors", e);
            throw new DataException("failed to get anchors");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Anchor> query(QueryTemplate<Anchor> template, Object... args) {
        return new ArrayList<Anchor>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Anchor anchor) {
        return anchor.id;
    }

    @Override
    public StringBuffer exportSingle(Anchor t) {
        StringBuffer sb = new StringBuffer("INSERT INTO anchors (id, name, type, stimuli, response) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.stimuli));
        sb.append(",");
        sb.append(escapeStr(t.response));
        sb.append(");");
        return sb;
    }

}
