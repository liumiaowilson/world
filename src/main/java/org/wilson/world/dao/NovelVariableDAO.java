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
import org.wilson.world.model.NovelVariable;

import com.mysql.jdbc.Statement;

public class NovelVariableDAO extends AbstractDAO<NovelVariable> {
    public static final String ITEM_TABLE_NAME = "novel_variables";
    
    private static final Logger logger = Logger.getLogger(NovelVariableDAO.class);

    @Override
    public void create(NovelVariable variable) {
        if(variable == null) {
            throw new DataException("NovelVariable should not be null");
        }
        if(StringUtils.isBlank(variable.name)) {
            throw new DataException("NovelVariable should have a valid name");
        }
        if(StringUtils.isBlank(variable.description)) {
            throw new DataException("NovelVariable should have a valid description");
        }
        if(StringUtils.isBlank(variable.defaultValue)) {
            throw new DataException("NovelVariable should have a valid defaultValue");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_variables(name, description, default_value) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, variable.name);
            ps.setString(2, variable.description);
            ps.setString(3, variable.defaultValue);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                variable.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel variable", e);
            throw new DataException("failed to create novel variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelVariable variable) {
        if(variable == null) {
            throw new DataException("NovelVariable should not be null");
        }
        if(StringUtils.isBlank(variable.name)) {
            throw new DataException("NovelVariable should have a valid name");
        }
        if(StringUtils.isBlank(variable.description)) {
            throw new DataException("NovelVariable should have a valid description");
        }
        if(StringUtils.isBlank(variable.defaultValue)) {
            throw new DataException("NovelVariable should have a valid defaultValue");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_variables set name = ?, description = ?, default_value = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, variable.name);
            ps.setString(2, variable.description);
            ps.setString(3, variable.defaultValue);
            ps.setInt(4, variable.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel variable", e);
            throw new DataException("failed to update novel variable");
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
            String sql = "delete from novel_variables where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel variable", e);
            throw new DataException("failed to delete novel variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelVariable get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_variables where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelVariable variable = new NovelVariable();
                variable.id = id;
                variable.name = rs.getString(2);
                variable.description = rs.getString(3);
                variable.defaultValue = rs.getString(4);
                return variable;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel variable", e);
            throw new DataException("failed to get novel variable");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelVariable> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_variables;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelVariable> variables = new ArrayList<NovelVariable>();
            while(rs.next()) {
            	NovelVariable variable = new NovelVariable();
                variable.id = rs.getInt(1);
                variable.name = rs.getString(2);
                variable.description = rs.getString(3);
                variable.defaultValue = rs.getString(4);
                variables.add(variable);
            }
            return variables;
        }
        catch(Exception e) {
            logger.error("failed to get novel variables", e);
            throw new DataException("failed to get novel variables");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelVariable> query(QueryTemplate<NovelVariable> template, Object... args) {
        return new ArrayList<NovelVariable>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelVariable variable) {
        return variable.id;
    }

    @Override
    public StringBuffer exportSingle(NovelVariable t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_variables (id, name, description, default_value) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.defaultValue));
        sb.append(");");
        return sb;
    }

}
