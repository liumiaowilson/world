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
import org.wilson.world.model.Quote;

import com.mysql.jdbc.Statement;

public class QuoteDAO extends AbstractDAO<Quote> {
    public static final String ITEM_TABLE_NAME = "quotes";
    
    private static final Logger logger = Logger.getLogger(QuoteDAO.class);

    @Override
    public void create(Quote quote) {
        if(quote == null) {
            throw new DataException("quote should not be null");
        }
        if(StringUtils.isBlank(quote.name)) {
            throw new DataException("quote should have a valid name");
        }
        if(StringUtils.isBlank(quote.content)) {
            throw new DataException("quote should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into quotes(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quote.name);
            ps.setString(2, quote.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                quote.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create quote", e);
            throw new DataException("failed to create quote");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Quote quote) {
        if(quote == null) {
            throw new DataException("quote should not be null");
        }
        if(StringUtils.isBlank(quote.name)) {
            throw new DataException("quote should have a valid name");
        }
        if(StringUtils.isBlank(quote.content)) {
            throw new DataException("quote should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update quotes set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, quote.name);
            ps.setString(2, quote.content);
            ps.setInt(3, quote.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update quote", e);
            throw new DataException("failed to update quote");
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
            String sql = "delete from quotes where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete quote", e);
            throw new DataException("failed to delete quote");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Quote get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quotes where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Quote quote = new Quote();
                quote.id = id;
                quote.name = rs.getString(2);
                quote.content = rs.getString(3);
                return quote;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get quote", e);
            throw new DataException("failed to get quote");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Quote> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quotes;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Quote> quotes = new ArrayList<Quote>();
            while(rs.next()) {
                Quote quote = new Quote();
                quote.id = rs.getInt(1);
                quote.name = rs.getString(2);
                quote.content = rs.getString(3);
                quotes.add(quote);
            }
            return quotes;
        }
        catch(Exception e) {
            logger.error("failed to get quotes", e);
            throw new DataException("failed to get quotes");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Quote> query(QueryTemplate<Quote> template, Object... args) {
        return new ArrayList<Quote>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Quote quote) {
        return quote.id;
    }

    @Override
    public StringBuffer exportSingle(Quote t) {
        StringBuffer sb = new StringBuffer("INSERT INTO quotes (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
