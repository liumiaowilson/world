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
import org.wilson.world.model.SkillData;

import com.mysql.jdbc.Statement;

public class SkillDataDAO extends AbstractDAO<SkillData> {
    public static final String ITEM_TABLE_NAME = "skill_data";
    
    private static final Logger logger = Logger.getLogger(SkillDataDAO.class);

    @Override
    public void create(SkillData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.type)) {
            throw new DataException("data should have a valid type");
        }
        if(StringUtils.isBlank(data.scope)) {
            throw new DataException("data should have a valid scope");
        }
        if(StringUtils.isBlank(data.target)) {
            throw new DataException("data should have a valid target");
        }
        if(StringUtils.isBlank(data.canTrigger)) {
            throw new DataException("data should have a valid canTrigger");
        }
        if(StringUtils.isBlank(data.trigger)) {
            throw new DataException("data should have a valid trigger");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into skill_data(name, description, type, scope, target, cost, cooldown, can_trigger, trigger_impl) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.type);
            ps.setString(4, data.scope);
            ps.setString(5, data.target);
            ps.setInt(6, data.cost);
            ps.setInt(7, data.cooldown);
            ps.setString(8, data.canTrigger);
            ps.setString(9, data.trigger);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                data.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
            throw new DataException("failed to create data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(SkillData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.type)) {
            throw new DataException("data should have a valid type");
        }
        if(StringUtils.isBlank(data.scope)) {
            throw new DataException("data should have a valid scope");
        }
        if(StringUtils.isBlank(data.target)) {
            throw new DataException("data should have a valid target");
        }
        if(StringUtils.isBlank(data.canTrigger)) {
            throw new DataException("data should have a valid canTrigger");
        }
        if(StringUtils.isBlank(data.trigger)) {
            throw new DataException("data should have a valid trigger");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update skill_data set name = ?, description = ?, type = ?, scope = ?, target = ?, cost = ?, cooldown = ?, can_trigger = ?, trigger_impl = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.type);
            ps.setString(4, data.scope);
            ps.setString(5, data.target);
            ps.setInt(6, data.cost);
            ps.setInt(7, data.cooldown);
            ps.setString(8, data.canTrigger);
            ps.setString(9, data.trigger);
            ps.setInt(10, data.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
            throw new DataException("failed to update data");
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
            String sql = "delete from skill_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            throw new DataException("failed to delete data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public SkillData get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from skill_data where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                SkillData data = new SkillData();
                data.id = id;
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.type = rs.getString(4);
                data.scope = rs.getString(5);
                data.target = rs.getString(6);
                data.cost = rs.getInt(7);
                data.cooldown = rs.getInt(8);
                data.canTrigger = rs.getString(9);
                data.trigger = rs.getString(10);
                return data;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
            throw new DataException("failed to get data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<SkillData> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from skill_data;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<SkillData> datas = new ArrayList<SkillData>();
            while(rs.next()) {
                SkillData data = new SkillData();
                data.id = rs.getInt(1);
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.type = rs.getString(4);
                data.scope = rs.getString(5);
                data.target = rs.getString(6);
                data.cost = rs.getInt(7);
                data.cooldown = rs.getInt(8);
                data.canTrigger = rs.getString(9);
                data.trigger = rs.getString(10);
                datas.add(data);
            }
            return datas;
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
            throw new DataException("failed to get datas");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<SkillData> query(QueryTemplate<SkillData> template, Object... args) {
        return new ArrayList<SkillData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(SkillData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(SkillData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO skill_data (id, name, description, type, scope, target, cost, cooldown, can_trigger, trigger_impl) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.scope));
        sb.append(",");
        sb.append(escapeStr(t.target));
        sb.append(",");
        sb.append(t.cost);
        sb.append(",");
        sb.append(t.cooldown);
        sb.append(",");
        sb.append(escapeStr(t.canTrigger));
        sb.append(",");
        sb.append(escapeStr(t.trigger));
        sb.append(");");
        return sb;
    }

}
