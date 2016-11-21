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
import org.wilson.world.model.ImageSet;

import com.mysql.jdbc.Statement;

public class ImageSetDAO extends AbstractDAO<ImageSet> {
    public static final String ITEM_TABLE_NAME = "image_sets";
    
    private static final Logger logger = Logger.getLogger(ImageSetDAO.class);

    @Override
    public void create(ImageSet set) {
        if(set == null) {
            throw new DataException("ImageSet should not be null");
        }
        if(StringUtils.isBlank(set.name)) {
            throw new DataException("ImageSet should have a valid name");
        }
        if(StringUtils.isBlank(set.content)) {
            throw new DataException("ImageSet should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into image_sets(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, set.name);
            ps.setString(2, set.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                set.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create image set", e);
            throw new DataException("failed to create image set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ImageSet set) {
        if(set == null) {
            throw new DataException("ImageSet should not be null");
        }
        if(StringUtils.isBlank(set.name)) {
            throw new DataException("ImageSet should have a valid name");
        }
        if(StringUtils.isBlank(set.content)) {
            throw new DataException("ImageSet should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update image_sets set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, set.name);
            ps.setString(2, set.content);
            ps.setInt(3, set.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update image set", e);
            throw new DataException("failed to update image set");
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
            String sql = "delete from image_sets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete image set", e);
            throw new DataException("failed to delete image set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ImageSet get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from image_sets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	ImageSet set = new ImageSet();
                set.id = id;
                set.name = rs.getString(2);
                set.content = rs.getString(3);
                return set;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get image set", e);
            throw new DataException("failed to get image set");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImageSet> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from image_sets;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ImageSet> sets = new ArrayList<ImageSet>();
            while(rs.next()) {
            	ImageSet set = new ImageSet();
                set.id = rs.getInt(1);
                set.name = rs.getString(2);
                set.content = rs.getString(3);
                sets.add(set);
            }
            return sets;
        }
        catch(Exception e) {
            logger.error("failed to get image sets", e);
            throw new DataException("failed to get image sets");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImageSet> query(QueryTemplate<ImageSet> template, Object... args) {
        return new ArrayList<ImageSet>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ImageSet set) {
        return set.id;
    }

    @Override
    public StringBuffer exportSingle(ImageSet t) {
        StringBuffer sb = new StringBuffer("INSERT INTO image_sets (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
