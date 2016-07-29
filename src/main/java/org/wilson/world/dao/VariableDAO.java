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
import org.wilson.world.model.Variable;

import com.mysql.jdbc.Statement;

public class VariableDAO extends AbstractDAO<Variable> {
    public static final String ITEM_TABLE_NAME = "variables";
    
    private static final Logger logger = Logger.getLogger(VariableDAO.class);

    @Override
    public void create(Variable variable) {
        if(variable == null) {
            throw new DataException("variable should not be null");
        }
        if(StringUtils.isBlank(variable.name)) {
            throw new DataException("variable should have a valid name");
        }
        if(StringUtils.isBlank(variable.expression)) {
            throw new DataException("variable should have a valid expression");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into variables(name, expression) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, variable.name);
            ps.setString(2, variable.expression);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                variable.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create variable", e);
            throw new DataException("failed to create variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Variable variable) {
        if(variable == null) {
            throw new DataException("variable should not be null");
        }
        if(StringUtils.isBlank(variable.name)) {
            throw new DataException("variable should have a valid name");
        }
        if(StringUtils.isBlank(variable.expression)) {
            throw new DataException("variable should have a valid expression");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update variables set name = ?, expression = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, variable.name);
            ps.setString(2, variable.expression);
            ps.setInt(3, variable.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update variable", e);
            throw new DataException("failed to update variable");
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
            String sql = "delete from variables where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete variable", e);
            throw new DataException("failed to delete variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Variable get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from variables where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Variable variable = new Variable();
                variable.id = id;
                variable.name = rs.getString(2);
                variable.expression = rs.getString(3);
                return variable;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get variable", e);
            throw new DataException("failed to get variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Variable> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from variables;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Variable> variables = new ArrayList<Variable>();
            while(rs.next()) {
                Variable variable = new Variable();
                variable.id = rs.getInt(1);
                variable.name = rs.getString(2);
                variable.expression = rs.getString(3);
                variables.add(variable);
            }
            return variables;
        }
        catch(Exception e) {
            logger.error("failed to get variables", e);
            throw new DataException("failed to get variables");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Variable> query(QueryTemplate<Variable> template, Object... args) {
        return new ArrayList<Variable>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Variable variable) {
        return variable.id;
    }

    @Override
    public StringBuffer exportSingle(Variable t) {
        StringBuffer sb = new StringBuffer("INSERT INTO variables (id, name, expression) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.expression));
        sb.append("');");
        return sb;
    }

}
