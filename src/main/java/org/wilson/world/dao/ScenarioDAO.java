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
import org.wilson.world.model.Scenario;

import com.mysql.jdbc.Statement;

public class ScenarioDAO extends AbstractDAO<Scenario> {
    public static final String ITEM_TABLE_NAME = "scenarios";
    
    private static final Logger logger = Logger.getLogger(ScenarioDAO.class);

    @Override
    public void create(Scenario scenario) {
        if(scenario == null) {
            throw new DataException("scenario should not be null");
        }
        if(StringUtils.isBlank(scenario.name)) {
            throw new DataException("scenario should have a valid name");
        }
        if(StringUtils.isBlank(scenario.stimuli)) {
            throw new DataException("scenario should have a valid stimuli");
        }
        if(StringUtils.isBlank(scenario.reaction)) {
            throw new DataException("scenario should have a valid reaction");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into scenarios(name, stimuli, reaction) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, scenario.name);
            ps.setString(2, scenario.stimuli);
            ps.setString(3, scenario.reaction);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                scenario.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create scenario", e);
            throw new DataException("failed to create scenario");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Scenario scenario) {
        if(scenario == null) {
            throw new DataException("scenario should not be null");
        }
        if(StringUtils.isBlank(scenario.name)) {
            throw new DataException("scenario should have a valid name");
        }
        if(StringUtils.isBlank(scenario.stimuli)) {
            throw new DataException("scenario should have a valid stimuli");
        }
        if(StringUtils.isBlank(scenario.reaction)) {
            throw new DataException("scenario should have a valid reaction");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update scenarios set name = ?, stimuli = ?, reaction = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, scenario.name);
            ps.setString(2, scenario.stimuli);
            ps.setString(3, scenario.reaction);
            ps.setInt(4, scenario.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update scenario", e);
            throw new DataException("failed to update scenario");
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
            String sql = "delete from scenarios where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete scenario", e);
            throw new DataException("failed to delete scenario");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Scenario get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from scenarios where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Scenario scenario = new Scenario();
                scenario.id = id;
                scenario.name = rs.getString(2);
                scenario.stimuli = rs.getString(3);
                scenario.reaction = rs.getString(4);
                return scenario;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get scenario", e);
            throw new DataException("failed to get scenario");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Scenario> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from scenarios;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Scenario> scenarios = new ArrayList<Scenario>();
            while(rs.next()) {
                Scenario scenario = new Scenario();
                scenario.id = rs.getInt(1);
                scenario.name = rs.getString(2);
                scenario.stimuli = rs.getString(3);
                scenario.reaction = rs.getString(4);
                scenarios.add(scenario);
            }
            return scenarios;
        }
        catch(Exception e) {
            logger.error("failed to get scenarios", e);
            throw new DataException("failed to get scenarios");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Scenario> query(QueryTemplate<Scenario> template, Object... args) {
        return new ArrayList<Scenario>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Scenario scenario) {
        return scenario.id;
    }

    @Override
    public StringBuffer exportSingle(Scenario t) {
        StringBuffer sb = new StringBuffer("INSERT INTO scenarios (id, name, stimuli, reaction) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.stimuli));
        sb.append(",");
        sb.append(escapeStr(t.reaction));
        sb.append(");");
        return sb;
    }

}
