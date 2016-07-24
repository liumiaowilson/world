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
import org.wilson.world.model.TaskAttrRule;

import com.mysql.jdbc.Statement;

public class TaskAttrRuleDAO extends AbstractDAO<TaskAttrRule> {
    public static final String ITEM_TABLE_NAME = "task_attr_rules";
    
    private static final Logger logger = Logger.getLogger(TaskAttrRuleDAO.class);

    @Override
    public void create(TaskAttrRule rule) {
        if(rule == null) {
            throw new DataException("task attr rule should not be null");
        }
        if(StringUtils.isBlank(rule.name)) {
            throw new DataException("task attr rule should have a valid name");
        }
        if(StringUtils.isBlank(rule.policy)) {
            throw new DataException("task attr rule should have a valid policy");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into task_attr_rules(name, priority, policy, impl) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, rule.name);
            ps.setInt(2, rule.priority);
            ps.setString(3, rule.policy);
            ps.setString(4, rule.impl);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                rule.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create task attr rule", e);
            throw new DataException("failed to create task attr rule");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(TaskAttrRule rule) {
        if(rule == null) {
            throw new DataException("task attr rule should not be null");
        }
        if(StringUtils.isBlank(rule.name)) {
            throw new DataException("task attr rule should have a valid name");
        }
        if(StringUtils.isBlank(rule.policy)) {
            throw new DataException("task attr rule should have a valid policy");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update task_attr_rules set name = ?, priority = ?, policy = ?, impl = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, rule.name);
            ps.setInt(2, rule.priority);
            ps.setString(3, rule.policy);
            ps.setString(4, rule.impl);
            ps.setInt(5, rule.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update task attr rule", e);
            throw new DataException("failed to update task attr rule");
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
            String sql = "delete from task_attr_rules where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete task attr rule", e);
            throw new DataException("failed to delete task attr rule");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public TaskAttrRule get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_attr_rules where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                TaskAttrRule rule = new TaskAttrRule();
                rule.id = id;
                rule.name = rs.getString(2);
                rule.priority = rs.getInt(3);
                rule.policy = rs.getString(4);
                rule.impl = rs.getString(5);
                return rule;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get task attr rule", e);
            throw new DataException("failed to get task attr rule");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskAttrRule> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from task_attr_rules;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<TaskAttrRule> rules = new ArrayList<TaskAttrRule>();
            while(rs.next()) {
                TaskAttrRule rule = new TaskAttrRule();
                rule.id = rs.getInt(1);
                rule.name = rs.getString(2);
                rule.priority = rs.getInt(3);
                rule.policy = rs.getString(4);
                rule.impl = rs.getString(5);
                rules.add(rule);
            }
            return rules;
        }
        catch(Exception e) {
            logger.error("failed to get task attr rules", e);
            throw new DataException("failed to get task attr rules");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<TaskAttrRule> query(QueryTemplate<TaskAttrRule> template, Object... args) {
        return new ArrayList<TaskAttrRule>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(TaskAttrRule rule) {
        return rule.id;
    }

    @Override
    public StringBuffer exportSingle(TaskAttrRule t) {
        StringBuffer sb = new StringBuffer("INSERT INTO task_attr_rules (id, name, priority, policy, impl) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("',");
        sb.append(t.priority);
        sb.append(",'");
        sb.append(escape(t.policy));
        sb.append("','");
        sb.append(escape(t.impl));
        sb.append("');");
        return sb;
    }

}
