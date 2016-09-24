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
import org.wilson.world.model.Plan;

import com.mysql.jdbc.Statement;

public class PlanDAO extends AbstractDAO<Plan> {
    public static final String ITEM_TABLE_NAME = "plans";
    
    private static final Logger logger = Logger.getLogger(PlanDAO.class);

    @Override
    public void create(Plan plan) {
        if(plan == null) {
            throw new DataException("Plan should not be null");
        }
        if(StringUtils.isBlank(plan.name)) {
            throw new DataException("Plan should have a valid name");
        }
        if(StringUtils.isBlank(plan.content)) {
            throw new DataException("Plan should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into plans(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, plan.name);
            ps.setString(2, plan.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                plan.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create plan", e);
            throw new DataException("failed to create plan");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Plan plan) {
        if(plan == null) {
            throw new DataException("Plan should not be null");
        }
        if(StringUtils.isBlank(plan.name)) {
            throw new DataException("Plan should have a valid name");
        }
        if(StringUtils.isBlank(plan.content)) {
            throw new DataException("Plan should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update plans set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, plan.name);
            ps.setString(2, plan.content);
            ps.setInt(3, plan.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update plan", e);
            throw new DataException("failed to update plan");
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
            String sql = "delete from plans where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete plan", e);
            throw new DataException("failed to delete plan");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Plan get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from plans where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Plan plan = new Plan();
                plan.id = id;
                plan.name = rs.getString(2);
                plan.content = rs.getString(3);
                return plan;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get plan", e);
            throw new DataException("failed to get plan");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Plan> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from plans;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Plan> plans = new ArrayList<Plan>();
            while(rs.next()) {
                Plan plan = new Plan();
                plan.id = rs.getInt(1);
                plan.name = rs.getString(2);
                plan.content = rs.getString(3);
                plans.add(plan);
            }
            return plans;
        }
        catch(Exception e) {
            logger.error("failed to get plans", e);
            throw new DataException("failed to get plans");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Plan> query(QueryTemplate<Plan> template, Object... args) {
        return new ArrayList<Plan>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Plan plan) {
        return plan.id;
    }

    @Override
    public StringBuffer exportSingle(Plan t) {
        StringBuffer sb = new StringBuffer("INSERT INTO plans (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
