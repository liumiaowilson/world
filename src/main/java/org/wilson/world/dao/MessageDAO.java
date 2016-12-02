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
import org.wilson.world.model.Message;

import com.mysql.jdbc.Statement;

public class MessageDAO extends AbstractDAO<Message> {
    public static final String ITEM_TABLE_NAME = "messages";
    
    private static final Logger logger = Logger.getLogger(MessageDAO.class);

    @Override
    public void create(Message message) {
        if(message == null) {
            throw new DataException("Message should not be null");
        }
        if(StringUtils.isBlank(message.name)) {
            throw new DataException("Message should have a valid name");
        }
        if(StringUtils.isBlank(message.topic)) {
            throw new DataException("Message should have a valid topic");
        }
        if(StringUtils.isBlank(message.content)) {
            throw new DataException("Message should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into messages(name, topic, content, time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.name);
            ps.setString(2, message.topic);
            ps.setString(3, message.content);
            ps.setLong(4, message.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                message.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create message", e);
            throw new DataException("failed to create message");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Message message) {
    	if(message == null) {
            throw new DataException("Message should not be null");
        }
        if(StringUtils.isBlank(message.name)) {
            throw new DataException("Message should have a valid name");
        }
        if(StringUtils.isBlank(message.topic)) {
            throw new DataException("Message should have a valid topic");
        }
        if(StringUtils.isBlank(message.content)) {
            throw new DataException("Message should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update messages set name = ?, topic = ?, content = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, message.name);
            ps.setString(2, message.topic);
            ps.setString(3, message.content);
            ps.setLong(4, message.time);
            ps.setInt(5, message.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update message", e);
            throw new DataException("failed to update message");
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
            String sql = "delete from messages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete message", e);
            throw new DataException("failed to delete message");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Message get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from messages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	Message message = new Message();
                message.id = id;
                message.name = rs.getString(2);
                message.topic = rs.getString(3);
                message.content = rs.getString(4);
                message.time = rs.getLong(5);
                return message;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get message", e);
            throw new DataException("failed to get message");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Message> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from messages;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Message> messages = new ArrayList<Message>();
            while(rs.next()) {
            	Message message = new Message();
                message.id = rs.getInt(1);
                message.name = rs.getString(2);
                message.topic = rs.getString(3);
                message.content = rs.getString(4);
                message.time = rs.getLong(5);
                messages.add(message);
            }
            return messages;
        }
        catch(Exception e) {
            logger.error("failed to get messages", e);
            throw new DataException("failed to get messages");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Message> query(QueryTemplate<Message> template, Object... args) {
        return new ArrayList<Message>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Message message) {
        return message.id;
    }

    @Override
    public StringBuffer exportSingle(Message t) {
        StringBuffer sb = new StringBuffer("INSERT INTO messages (id, name, topic, content, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.topic));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
