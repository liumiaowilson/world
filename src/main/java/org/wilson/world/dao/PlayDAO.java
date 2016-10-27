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
import org.wilson.world.model.Play;

import com.mysql.jdbc.Statement;

public class PlayDAO extends AbstractDAO<Play> {
    public static final String ITEM_TABLE_NAME = "plays";
    
    private static final Logger logger = Logger.getLogger(PlayDAO.class);

    @Override
    public void create(Play play) {
        if(play == null) {
            throw new DataException("Play should not be null");
        }
        if(StringUtils.isBlank(play.name)) {
            throw new DataException("Play should have a valid name");
        }
        if(StringUtils.isBlank(play.source)) {
            throw new DataException("Play should have a valid source");
        }
        if(StringUtils.isBlank(play.content)) {
            throw new DataException("Play should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into plays(name, source, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, play.name);
            ps.setString(2, play.source);
            ps.setString(3, play.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                play.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create play", e);
            throw new DataException("failed to create play");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Play play) {
    	if(play == null) {
            throw new DataException("Play should not be null");
        }
        if(StringUtils.isBlank(play.name)) {
            throw new DataException("Play should have a valid name");
        }
        if(StringUtils.isBlank(play.source)) {
            throw new DataException("Play should have a valid source");
        }
        if(StringUtils.isBlank(play.content)) {
            throw new DataException("Play should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update plays set name = ?, source = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, play.name);
            ps.setString(2, play.source);
            ps.setString(3, play.content);
            ps.setInt(4, play.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update play", e);
            throw new DataException("failed to update play");
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
            String sql = "delete from plays where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete play", e);
            throw new DataException("failed to delete play");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Play get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from plays where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Play play = new Play();
                play.id = id;
                play.name = rs.getString(2);
                play.source = rs.getString(3);
                play.content = rs.getString(4);
                return play;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get play", e);
            throw new DataException("failed to get play");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Play> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from plays;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Play> plays = new ArrayList<Play>();
            while(rs.next()) {
            	Play play = new Play();
                play.id = rs.getInt(1);
                play.name = rs.getString(2);
                play.source = rs.getString(3);
                play.content = rs.getString(4);
                plays.add(play);
            }
            return plays;
        }
        catch(Exception e) {
            logger.error("failed to get plays", e);
            throw new DataException("failed to get plays");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Play> query(QueryTemplate<Play> template, Object... args) {
        return new ArrayList<Play>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Play play) {
        return play.id;
    }

    @Override
    public StringBuffer exportSingle(Play t) {
        StringBuffer sb = new StringBuffer("INSERT INTO plays (id, name, source, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.source));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
