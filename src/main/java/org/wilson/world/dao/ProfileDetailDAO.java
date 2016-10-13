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
import org.wilson.world.model.ProfileDetail;

import com.mysql.jdbc.Statement;

public class ProfileDetailDAO extends AbstractDAO<ProfileDetail> {
    public static final String ITEM_TABLE_NAME = "profile_details";
    
    private static final Logger logger = Logger.getLogger(ProfileDetailDAO.class);

    @Override
    public void create(ProfileDetail detail) {
        if(detail == null) {
            throw new DataException("ProfileDetail should not be null");
        }
        if(StringUtils.isBlank(detail.name)) {
            throw new DataException("ProfileDetail should have a valid name");
        }
        if(StringUtils.isBlank(detail.type)) {
            throw new DataException("ProfileDetail should have a valid type");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("ProfileDetail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into profile_details(name, type, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, detail.name);
            ps.setString(2, detail.type);
            ps.setString(3, detail.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                detail.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create profile detail", e);
            throw new DataException("failed to create profile detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ProfileDetail detail) {
        if(detail == null) {
            throw new DataException("ProfileDetail should not be null");
        }
        if(StringUtils.isBlank(detail.name)) {
            throw new DataException("ProfileDetail should have a valid name");
        }
        if(StringUtils.isBlank(detail.type)) {
            throw new DataException("ProfileDetail should have a valid type");
        }
        if(StringUtils.isBlank(detail.content)) {
            throw new DataException("ProfileDetail should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update profile_details set name = ?, type = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, detail.name);
            ps.setString(2, detail.type);
            ps.setString(3, detail.content);
            ps.setInt(4, detail.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update profile detail", e);
            throw new DataException("failed to update profile detail");
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
            String sql = "delete from profile_details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete profile detail", e);
            throw new DataException("failed to delete profile detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ProfileDetail get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from profile_details where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ProfileDetail detail = new ProfileDetail();
                detail.id = id;
                detail.name = rs.getString(2);
                detail.type = rs.getString(3);
                detail.content = rs.getString(4);
                return detail;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get profile detail", e);
            throw new DataException("failed to get profile detail");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ProfileDetail> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from profile_details;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ProfileDetail> details = new ArrayList<ProfileDetail>();
            while(rs.next()) {
                ProfileDetail detail = new ProfileDetail();
                detail.id = rs.getInt(1);
                detail.name = rs.getString(2);
                detail.type = rs.getString(3);
                detail.content = rs.getString(4);
                details.add(detail);
            }
            return details;
        }
        catch(Exception e) {
            logger.error("failed to get profile details", e);
            throw new DataException("failed to get profile details");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ProfileDetail> query(QueryTemplate<ProfileDetail> template, Object... args) {
        return new ArrayList<ProfileDetail>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ProfileDetail detail) {
        return detail.id;
    }

    @Override
    public StringBuffer exportSingle(ProfileDetail t) {
        StringBuffer sb = new StringBuffer("INSERT INTO profile_details (id, name, type, content) VALUES (");
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
