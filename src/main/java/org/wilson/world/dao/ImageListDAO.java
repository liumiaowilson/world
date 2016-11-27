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
import org.wilson.world.model.ImageList;

import com.mysql.jdbc.Statement;

public class ImageListDAO extends AbstractDAO<ImageList> {
    public static final String ITEM_TABLE_NAME = "image_lists";
    
    private static final Logger logger = Logger.getLogger(ImageListDAO.class);

    @Override
    public void create(ImageList list) {
        if(list == null) {
            throw new DataException("ImageList should not be null");
        }
        if(StringUtils.isBlank(list.name)) {
            throw new DataException("ImageList should have a valid name");
        }
        if(StringUtils.isBlank(list.content)) {
            throw new DataException("ImageList should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into image_lists(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, list.name);
            ps.setString(2, list.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                list.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create image list", e);
            throw new DataException("failed to create image list");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ImageList list) {
        if(list == null) {
            throw new DataException("ImageList should not be null");
        }
        if(StringUtils.isBlank(list.name)) {
            throw new DataException("ImageList should have a valid name");
        }
        if(StringUtils.isBlank(list.content)) {
            throw new DataException("ImageList should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update image_lists set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, list.name);
            ps.setString(2, list.content);
            ps.setInt(3, list.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update image list", e);
            throw new DataException("failed to update image list");
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
            String sql = "delete from image_lists where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete image list", e);
            throw new DataException("failed to delete image list");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ImageList get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from image_lists where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	ImageList list = new ImageList();
                list.id = id;
                list.name = rs.getString(2);
                list.content = rs.getString(3);
                return list;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get image list", e);
            throw new DataException("failed to get image list");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImageList> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from image_lists;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ImageList> lists = new ArrayList<ImageList>();
            while(rs.next()) {
            	ImageList list = new ImageList();
                list.id = rs.getInt(1);
                list.name = rs.getString(2);
                list.content = rs.getString(3);
                lists.add(list);
            }
            return lists;
        }
        catch(Exception e) {
            logger.error("failed to get image lists", e);
            throw new DataException("failed to get image lists");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ImageList> query(QueryTemplate<ImageList> template, Object... args) {
        return new ArrayList<ImageList>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ImageList list) {
        return list.id;
    }

    @Override
    public StringBuffer exportSingle(ImageList t) {
        StringBuffer sb = new StringBuffer("INSERT INTO image_lists (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
