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
import org.wilson.world.model.NovelStat;

import com.mysql.jdbc.Statement;

public class NovelStatDAO extends AbstractDAO<NovelStat> {
    public static final String ITEM_TABLE_NAME = "novel_stats";
    
    private static final Logger logger = Logger.getLogger(NovelStatDAO.class);

    @Override
    public void create(NovelStat stat) {
        if(stat == null) {
            throw new DataException("NovelStat should not be null");
        }
        if(StringUtils.isBlank(stat.docId)) {
            throw new DataException("NovelStat should have a valid docId");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_stats(doc_id, time) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, stat.docId);
            ps.setLong(2, stat.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                stat.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel stat", e);
            throw new DataException("failed to create novel stat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelStat stat) {
        if(stat == null) {
            throw new DataException("NovelStat should not be null");
        }
        if(StringUtils.isBlank(stat.docId)) {
            throw new DataException("NovelStat should have a valid docId");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_stats set doc_id = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, stat.docId);
            ps.setLong(2, stat.time);
            ps.setInt(3, stat.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel stat", e);
            throw new DataException("failed to update novel stat");
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
            String sql = "delete from novel_stats where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel stat", e);
            throw new DataException("failed to delete novel stat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelStat get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_stats where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelStat stat = new NovelStat();
                stat.id = id;
                stat.docId = rs.getString(2);
                stat.time = rs.getLong(3);
                return stat;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel stat", e);
            throw new DataException("failed to get novel stat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelStat> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_stats;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelStat> stats = new ArrayList<NovelStat>();
            while(rs.next()) {
            	NovelStat stat = new NovelStat();
                stat.id = rs.getInt(1);
                stat.docId = rs.getString(2);
                stat.time = rs.getLong(3);
                stats.add(stat);
            }
            return stats;
        }
        catch(Exception e) {
            logger.error("failed to get novel stats", e);
            throw new DataException("failed to get novel stats");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelStat> query(QueryTemplate<NovelStat> template, Object... args) {
        return new ArrayList<NovelStat>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelStat stat) {
        return stat.id;
    }

    @Override
    public StringBuffer exportSingle(NovelStat t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_stats (id, doc_id, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.docId));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
