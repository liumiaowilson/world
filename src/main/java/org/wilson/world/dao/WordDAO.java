package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.Word;

import com.mysql.jdbc.Statement;

public class WordDAO extends AbstractDAO<Word> {
    public static final String ITEM_TABLE_NAME = "words";
    
    private static final Logger logger = Logger.getLogger(WordDAO.class);

    @Override
    public void create(Word word) {
        if(word == null) {
            throw new DataException("word should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into words(card_id, step, time) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, word.cardId);
            ps.setInt(2, word.step);
            ps.setLong(3, word.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                word.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create word", e);
            throw new DataException("failed to create word");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Word word) {
        if(word == null) {
            throw new DataException("word should not be null");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update words set card_id = ?, step = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, word.cardId);
            ps.setInt(2, word.step);
            ps.setLong(3, word.time);
            ps.setInt(4, word.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update word", e);
            throw new DataException("failed to update word");
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
            String sql = "delete from words where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete word", e);
            throw new DataException("failed to delete word");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Word get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from words where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Word word = new Word();
                word.id = id;
                word.cardId = rs.getInt(2);
                word.step = rs.getInt(3);
                word.time = rs.getLong(4);
                return word;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get word", e);
            throw new DataException("failed to get word");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Word> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from words;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Word> words = new ArrayList<Word>();
            while(rs.next()) {
                Word word = new Word();
                word.id = rs.getInt(1);
                word.cardId = rs.getInt(2);
                word.step = rs.getInt(3);
                word.time = rs.getLong(4);
                words.add(word);
            }
            return words;
        }
        catch(Exception e) {
            logger.error("failed to get words", e);
            throw new DataException("failed to get words");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Word> query(QueryTemplate<Word> template, Object... args) {
        return new ArrayList<Word>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Word word) {
        return word.id;
    }

    @Override
    public StringBuffer exportSingle(Word t) {
        StringBuffer sb = new StringBuffer("INSERT INTO words (id, card_id, step, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.cardId);
        sb.append(",");
        sb.append(t.step);
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
