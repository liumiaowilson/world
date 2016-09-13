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
import org.wilson.world.model.Emotion;

import com.mysql.jdbc.Statement;

public class EmotionDAO extends AbstractDAO<Emotion> {
    public static final String ITEM_TABLE_NAME = "emotions";
    
    private static final Logger logger = Logger.getLogger(EmotionDAO.class);

    @Override
    public void create(Emotion emotion) {
        if(emotion == null) {
            throw new DataException("Emotion should not be null");
        }
        if(StringUtils.isBlank(emotion.name)) {
            throw new DataException("Emotion should have a valid name");
        }
        if(StringUtils.isBlank(emotion.description)) {
            throw new DataException("Emotion should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into emotions(name, description, ecstacy, grief, admiration, loathing, rage, terror, vigilance, amazement) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, emotion.name);
            ps.setString(2, emotion.description);
            ps.setInt(3, emotion.ecstacy);
            ps.setInt(4, emotion.grief);
            ps.setInt(5, emotion.admiration);;
            ps.setInt(6, emotion.loathing);
            ps.setInt(7, emotion.rage);
            ps.setInt(8, emotion.terror);
            ps.setInt(9, emotion.vigilance);
            ps.setInt(10, emotion.amazement);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                emotion.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create emotion", e);
            throw new DataException("failed to create emotion");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Emotion emotion) {
        if(emotion == null) {
            throw new DataException("Emotion should not be null");
        }
        if(StringUtils.isBlank(emotion.name)) {
            throw new DataException("Emotion should have a valid name");
        }
        if(StringUtils.isBlank(emotion.description)) {
            throw new DataException("Emotion should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update emotions set name = ?, description = ?, ecstacy = ?, grief = ?, admiration = ?, loathing = ?, rage = ?, terror = ?, vigilance = ?, amazement = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, emotion.name);
            ps.setString(2, emotion.description);
            ps.setInt(3, emotion.ecstacy);
            ps.setInt(4, emotion.grief);
            ps.setInt(5, emotion.admiration);
            ps.setInt(6, emotion.loathing);
            ps.setInt(7, emotion.rage);
            ps.setInt(8, emotion.terror);
            ps.setInt(9, emotion.vigilance);
            ps.setInt(10, emotion.amazement);
            ps.setInt(11, emotion.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update emotion", e);
            throw new DataException("failed to update emotion");
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
            String sql = "delete from emotions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete emotion", e);
            throw new DataException("failed to delete emotion");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Emotion get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from emotions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Emotion emotion = new Emotion();
                emotion.id = id;
                emotion.name = rs.getString(2);
                emotion.description = rs.getString(3);
                emotion.ecstacy = rs.getInt(4);
                emotion.grief = rs.getInt(5);
                emotion.admiration = rs.getInt(6);
                emotion.loathing = rs.getInt(7);
                emotion.rage = rs.getInt(8);
                emotion.terror = rs.getInt(9);
                emotion.vigilance = rs.getInt(10);
                emotion.amazement = rs.getInt(11);
                return emotion;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get emotion", e);
            throw new DataException("failed to get emotion");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Emotion> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from emotions;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Emotion> emotions = new ArrayList<Emotion>();
            while(rs.next()) {
                Emotion emotion = new Emotion();
                emotion.id = rs.getInt(1);
                emotion.name = rs.getString(2);
                emotion.description = rs.getString(3);
                emotion.ecstacy = rs.getInt(4);
                emotion.grief = rs.getInt(5);
                emotion.admiration = rs.getInt(6);
                emotion.loathing = rs.getInt(7);
                emotion.rage = rs.getInt(8);
                emotion.terror = rs.getInt(9);
                emotion.vigilance = rs.getInt(10);
                emotion.amazement = rs.getInt(11);
                emotions.add(emotion);
            }
            return emotions;
        }
        catch(Exception e) {
            logger.error("failed to get emotions", e);
            throw new DataException("failed to get emotions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Emotion> query(QueryTemplate<Emotion> template, Object... args) {
        return new ArrayList<Emotion>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Emotion emotion) {
        return emotion.id;
    }

    @Override
    public StringBuffer exportSingle(Emotion t) {
        StringBuffer sb = new StringBuffer("INSERT INTO emotions (id, name, description, ecstacy, grief, admiration, loathing, rage, terror, vigilance, amazement) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(t.ecstacy);
        sb.append(",");
        sb.append(t.grief);
        sb.append(",");
        sb.append(t.admiration);
        sb.append(",");
        sb.append(t.loathing);
        sb.append(",");
        sb.append(t.rage);
        sb.append(",");
        sb.append(t.terror);
        sb.append(",");
        sb.append(t.vigilance);
        sb.append(",");
        sb.append(t.amazement);
        sb.append(");");
        return sb;
    }

}
