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
import org.wilson.world.model.Chat;

import com.mysql.jdbc.Statement;

public class ChatDAO extends AbstractDAO<Chat> {
    public static final String ITEM_TABLE_NAME = "chats";
    
    private static final Logger logger = Logger.getLogger(ChatDAO.class);

    @Override
    public void create(Chat chat) {
        if(chat == null) {
            throw new DataException("Chat should not be null");
        }
        if(StringUtils.isBlank(chat.name)) {
            throw new DataException("Chat should have a valid name");
        }
        if(StringUtils.isBlank(chat.content)) {
            throw new DataException("Chat should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into chats(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, chat.name);
            ps.setString(2, chat.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                chat.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create chat", e);
            throw new DataException("failed to create chat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Chat chat) {
        if(chat == null) {
            throw new DataException("Chat should not be null");
        }
        if(StringUtils.isBlank(chat.name)) {
            throw new DataException("Chat should have a valid name");
        }
        if(StringUtils.isBlank(chat.content)) {
            throw new DataException("Chat should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update chats set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, chat.name);
            ps.setString(2, chat.content);
            ps.setInt(3, chat.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update chat", e);
            throw new DataException("failed to update chat");
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
            String sql = "delete from chats where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete chat", e);
            throw new DataException("failed to delete chat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Chat get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from chats where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Chat chat = new Chat();
                chat.id = id;
                chat.name = rs.getString(2);
                chat.content = rs.getString(3);
                return chat;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get chat", e);
            throw new DataException("failed to get chat");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Chat> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from chats;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Chat> chats = new ArrayList<Chat>();
            while(rs.next()) {
                Chat chat = new Chat();
                chat.id = rs.getInt(1);
                chat.name = rs.getString(2);
                chat.content = rs.getString(3);
                chats.add(chat);
            }
            return chats;
        }
        catch(Exception e) {
            logger.error("failed to get chats", e);
            throw new DataException("failed to get chats");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Chat> query(QueryTemplate<Chat> template, Object... args) {
        return new ArrayList<Chat>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Chat chat) {
        return chat.id;
    }

    @Override
    public StringBuffer exportSingle(Chat t) {
        StringBuffer sb = new StringBuffer("INSERT INTO chats (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
