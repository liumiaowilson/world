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
import org.wilson.world.model.NovelTicket;

import com.mysql.jdbc.Statement;

public class NovelTicketDAO extends AbstractDAO<NovelTicket> {
    public static final String ITEM_TABLE_NAME = "novel_tickets";
    
    private static final Logger logger = Logger.getLogger(NovelTicketDAO.class);

    @Override
    public void create(NovelTicket ticket) {
        if(ticket == null) {
            throw new DataException("NovelTicket should not be null");
        }
        if(StringUtils.isBlank(ticket.name)) {
            throw new DataException("NovelTicket should have a valid name");
        }
        if(StringUtils.isBlank(ticket.description)) {
            throw new DataException("NovelTicket should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_tickets(doc_id, name, description) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ticket.docId);
            ps.setString(2, ticket.name);
            ps.setString(3, ticket.description);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                ticket.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel ticket", e);
            throw new DataException("failed to create novel ticket");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelTicket ticket) {
        if(ticket == null) {
            throw new DataException("NovelTicket should not be null");
        }
        if(StringUtils.isBlank(ticket.name)) {
            throw new DataException("NovelTicket should have a valid name");
        }
        if(StringUtils.isBlank(ticket.description)) {
            throw new DataException("NovelTicket should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_tickets set doc_id = ?, name = ?, description = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, ticket.docId);
            ps.setString(2, ticket.name);
            ps.setString(3, ticket.description);
            ps.setInt(4, ticket.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel ticket", e);
            throw new DataException("failed to update novel ticket");
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
            String sql = "delete from novel_tickets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel ticket", e);
            throw new DataException("failed to delete novel ticket");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelTicket get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_tickets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelTicket ticket = new NovelTicket();
                ticket.id = id;
                ticket.docId = rs.getString(2);
                ticket.name = rs.getString(3);
                ticket.description = rs.getString(4);
                return ticket;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel ticket", e);
            throw new DataException("failed to get novel ticket");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelTicket> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_tickets;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelTicket> tickets = new ArrayList<NovelTicket>();
            while(rs.next()) {
            	NovelTicket ticket = new NovelTicket();
                ticket.id = rs.getInt(1);
                ticket.docId = rs.getString(2);
                ticket.name = rs.getString(3);
                ticket.description = rs.getString(4);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch(Exception e) {
            logger.error("failed to get novel tickets", e);
            throw new DataException("failed to get novel tickets");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelTicket> query(QueryTemplate<NovelTicket> template, Object... args) {
        return new ArrayList<NovelTicket>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelTicket ticket) {
        return ticket.id;
    }

    @Override
    public StringBuffer exportSingle(NovelTicket t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_tickets (id, doc_id, name, description) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.docId);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(");");
        return sb;
    }

}
