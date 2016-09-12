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
import org.wilson.world.model.Quest;

import com.mysql.jdbc.Statement;

public class QuestDAO extends AbstractDAO<Quest> {
    public static final String ITEM_TABLE_NAME = "quests";
    
    private static final Logger logger = Logger.getLogger(QuestDAO.class);

    @Override
    public void create(Quest quest) {
        if(quest == null) {
            throw new DataException("quest should not be null");
        }
        if(StringUtils.isBlank(quest.name)) {
            throw new DataException("quest should have a valid name");
        }
        if(StringUtils.isBlank(quest.content)) {
            throw new DataException("quest should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into quests(def_id, name, content, time) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, quest.defId);
            ps.setString(2, quest.name);
            ps.setString(3, quest.content);
            ps.setLong(4, quest.time);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                quest.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create quest", e);
            throw new DataException("failed to create quest");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Quest quest) {
        if(quest == null) {
            throw new DataException("quest should not be null");
        }
        if(StringUtils.isBlank(quest.name)) {
            throw new DataException("quest should have a valid name");
        }
        if(StringUtils.isBlank(quest.content)) {
            throw new DataException("quest should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update quests set def_id = ?, name = ?, content = ?, time = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, quest.defId);
            ps.setString(2, quest.name);
            ps.setString(3, quest.content);
            ps.setLong(4, quest.time);
            ps.setInt(5, quest.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update quest", e);
            throw new DataException("failed to update quest");
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
            String sql = "delete from quests where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete quest", e);
            throw new DataException("failed to delete quest");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Quest get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quests where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Quest quest = new Quest();
                quest.id = id;
                quest.defId = rs.getInt(2);
                quest.name = rs.getString(3);
                quest.content = rs.getString(4);
                quest.time = rs.getLong(5);
                return quest;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get quest", e);
            throw new DataException("failed to get quest");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Quest> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quests;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Quest> quests = new ArrayList<Quest>();
            while(rs.next()) {
                Quest quest = new Quest();
                quest.id = rs.getInt(1);
                quest.defId = rs.getInt(2);
                quest.name = rs.getString(3);
                quest.content = rs.getString(4);
                quest.time = rs.getLong(5);
                quests.add(quest);
            }
            return quests;
        }
        catch(Exception e) {
            logger.error("failed to get quests", e);
            throw new DataException("failed to get quests");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Quest> query(QueryTemplate<Quest> template, Object... args) {
        return new ArrayList<Quest>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Quest quest) {
        return quest.id;
    }

    @Override
    public StringBuffer exportSingle(Quest t) {
        StringBuffer sb = new StringBuffer("INSERT INTO quests (id, def_id, name, content, time) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.defId);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(t.time);
        sb.append(");");
        return sb;
    }

}
