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
import org.wilson.world.model.RoleDetail;

import com.mysql.jdbc.Statement;

public class RoleDetailDAO extends AbstractDAO<RoleDetail> {
    public static final String ITEM_TABLE_NAME = "role_details";
    
    private static final Logger logger = Logger.getLogger(RoleDetailDAO.class);

    @Override
    public void create(RoleDetail detail) {
        if(detail == null) {
            throw new DataException("RoleDetail should not be null");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("RoleDetail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into role_details(role_id, role_attr_id, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, detail.roleId);
            ps.setInt(2, detail.roleAttrId);;
            ps.setString(3, detail.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                detail.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create role detail", e);
            throw new DataException("failed to create role detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(RoleDetail detail) {
        if(detail == null) {
            throw new DataException("RoleDetail should not be null");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("RoleDetail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update role_details set role_id = ?, role_attr_id = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, detail.roleId);
            ps.setInt(2, detail.roleAttrId);
            ps.setString(3, detail.content);
            ps.setInt(4, detail.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update role detail", e);
            throw new DataException("failed to update role detail");
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
            String sql = "delete from role_details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete role detail", e);
            throw new DataException("failed to delete role detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public RoleDetail get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from role_details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                RoleDetail detail = new RoleDetail();
                detail.id = id;
                detail.roleId = rs.getInt(2);
                detail.roleAttrId = rs.getInt(3);
                detail.content = rs.getString(4);
                return detail;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get role detail", e);
            throw new DataException("failed to get role detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RoleDetail> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from role_details;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<RoleDetail> details = new ArrayList<RoleDetail>();
            while(rs.next()) {
                RoleDetail detail = new RoleDetail();
                detail.id = rs.getInt(1);
                detail.roleId = rs.getInt(2);
                detail.roleAttrId = rs.getInt(3);
                detail.content = rs.getString(4);
                details.add(detail);
            }
            return details;
        }
        catch(Exception e) {
            logger.error("failed to get role details", e);
            throw new DataException("failed to get role details");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<RoleDetail> query(QueryTemplate<RoleDetail> template, Object... args) {
        return new ArrayList<RoleDetail>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(RoleDetail detail) {
        return detail.id;
    }

    @Override
    public StringBuffer exportSingle(RoleDetail t) {
        StringBuffer sb = new StringBuffer("INSERT INTO role_details (id, role_id, role_attr_id, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.roleId);
        sb.append(",");
        sb.append(t.roleAttrId);
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
