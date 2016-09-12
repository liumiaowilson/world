package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.ArticleSpeedInfo;

import com.mysql.jdbc.Statement;

public class ArticleSpeedInfoDAO extends AbstractDAO<ArticleSpeedInfo> {
    public static final String ITEM_TABLE_NAME = "article_speed_infos";
    
    private static final Logger logger = Logger.getLogger(ArticleSpeedInfoDAO.class);

    @Override
    public void create(ArticleSpeedInfo info) {
        if(info == null) {
            throw new DataException("info should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into article_speed_infos(words, time) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, info.words);
            ps.setLong(2, info.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                info.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create info", e);
            throw new DataException("failed to create info");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(ArticleSpeedInfo info) {
        if(info == null) {
            throw new DataException("info should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update article_speed_infos set words = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, info.words);
            ps.setLong(2, info.time);
            ps.setInt(3, info.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update info", e);
            throw new DataException("failed to update info");
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
            String sql = "delete from article_speed_infos where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete info", e);
            throw new DataException("failed to delete info");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public ArticleSpeedInfo get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from article_speed_infos where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                ArticleSpeedInfo info = new ArticleSpeedInfo();
                info.id = id;
                info.words = rs.getInt(2);
                info.time = rs.getLong(3);
                return info;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get info", e);
            throw new DataException("failed to get info");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ArticleSpeedInfo> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from article_speed_infos;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<ArticleSpeedInfo> infos = new ArrayList<ArticleSpeedInfo>();
            while(rs.next()) {
                ArticleSpeedInfo info = new ArticleSpeedInfo();
                info.id = rs.getInt(1);
                info.words = rs.getInt(2);
                info.time = rs.getLong(3);
                infos.add(info);
            }
            return infos;
        }
        catch(Exception e) {
            logger.error("failed to get infos", e);
            throw new DataException("failed to get infos");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<ArticleSpeedInfo> query(QueryTemplate<ArticleSpeedInfo> template, Object... args) {
        return new ArrayList<ArticleSpeedInfo>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(ArticleSpeedInfo info) {
        return info.id;
    }

    @Override
    public StringBuffer exportSingle(ArticleSpeedInfo t) {
        StringBuffer sb = new StringBuffer("INSERT INTO article_speed_infos (id, words, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.words);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
