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
import org.wilson.world.model.FlashCard;

import com.mysql.jdbc.Statement;

public class FlashCardDAO extends AbstractDAO<FlashCard> {
    public static final String ITEM_TABLE_NAME = "flashcards";
    
    private static final Logger logger = Logger.getLogger(FlashCardDAO.class);

    @Override
    public void create(FlashCard card) {
        if(card == null) {
            throw new DataException("card should not be null");
        }
        if(StringUtils.isBlank(card.name)) {
            throw new DataException("card should have a valid name");
        }
        if(StringUtils.isBlank(card.top)) {
            throw new DataException("card should have a valid top");
        }
        if(StringUtils.isBlank(card.bottom)) {
            throw new DataException("card should have a valid bottom");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into flashcards(name, set_id, top, bottom) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, card.name);
            ps.setInt(2, card.setId);
            ps.setString(3, card.top);
            ps.setString(4, card.bottom);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                card.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create card", e);
            throw new DataException("failed to create card");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(FlashCard card) {
        if(card == null) {
            throw new DataException("card should not be null");
        }
        if(StringUtils.isBlank(card.name)) {
            throw new DataException("card should have a valid name");
        }
        if(StringUtils.isBlank(card.top)) {
            throw new DataException("card should have a valid top");
        }
        if(StringUtils.isBlank(card.bottom)) {
            throw new DataException("card should have a valid bottom");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update flashcards set name = ?, set_id = ?, top = ?, bottom = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, card.name);
            ps.setInt(2, card.setId);
            ps.setString(3, card.top);
            ps.setString(4, card.bottom);
            ps.setInt(5, card.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update card", e);
            throw new DataException("failed to update card");
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
            String sql = "delete from flashcards where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete card", e);
            throw new DataException("failed to delete card");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public FlashCard get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from flashcards where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                FlashCard card = new FlashCard();
                card.id = id;
                card.name = rs.getString(2);
                card.setId = rs.getInt(3);
                card.top = rs.getString(4);
                card.bottom = rs.getString(5);
                return card;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get card", e);
            throw new DataException("failed to get card");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<FlashCard> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from flashcards;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<FlashCard> cards = new ArrayList<FlashCard>();
            while(rs.next()) {
                FlashCard card = new FlashCard();
                card.id = rs.getInt(1);
                card.name = rs.getString(2);
                card.setId = rs.getInt(3);
                card.top = rs.getString(4);
                card.bottom = rs.getString(5);
                cards.add(card);
            }
            return cards;
        }
        catch(Exception e) {
            logger.error("failed to get cards", e);
            throw new DataException("failed to get cards");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<FlashCard> query(QueryTemplate<FlashCard> template, Object... args) {
        return new ArrayList<FlashCard>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(FlashCard card) {
        return card.id;
    }

    @Override
    public StringBuffer exportSingle(FlashCard t) {
        StringBuffer sb = new StringBuffer("INSERT INTO flashcards (id, name, set_id, top, bottom) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(t.setId);
        sb.append(",");
        sb.append(escapeStr(t.top));
        sb.append(",");
        sb.append(escapeStr(t.bottom));
        sb.append(");");
        return sb;
    }

}
