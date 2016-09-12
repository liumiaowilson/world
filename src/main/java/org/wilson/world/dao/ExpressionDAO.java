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
import org.wilson.world.model.Expression;

import com.mysql.jdbc.Statement;

public class ExpressionDAO extends AbstractDAO<Expression> {
    public static final String ITEM_TABLE_NAME = "expressions";
    
    private static final Logger logger = Logger.getLogger(ExpressionDAO.class);

    @Override
    public void create(Expression expression) {
        if(expression == null) {
            throw new DataException("Expression should not be null");
        }
        if(StringUtils.isBlank(expression.name)) {
            throw new DataException("Expression should have a valid name");
        }
        if(StringUtils.isBlank(expression.keywords)) {
            throw new DataException("Expression should have a valid keywords");
        }
        if(StringUtils.isBlank(expression.content)) {
            throw new DataException("Expression should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into expressions(name, keywords, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, expression.name);
            ps.setString(2, expression.keywords);
            ps.setString(3, expression.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                expression.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create expression", e);
            throw new DataException("failed to create expression");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Expression expression) {
        if(expression == null) {
            throw new DataException("Expression should not be null");
        }
        if(StringUtils.isBlank(expression.name)) {
            throw new DataException("Expression should have a valid name");
        }
        if(StringUtils.isBlank(expression.keywords)) {
            throw new DataException("Expression should have a valid keywords");
        }
        if(StringUtils.isBlank(expression.content)) {
            throw new DataException("Expression should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update expressions set name = ?, keywords = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, expression.name);
            ps.setString(2, expression.keywords);
            ps.setString(3, expression.content);
            ps.setInt(4, expression.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update expression", e);
            throw new DataException("failed to update expression");
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
            String sql = "delete from expressions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete expression", e);
            throw new DataException("failed to delete expression");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Expression get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from expressions where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Expression expression = new Expression();
                expression.id = id;
                expression.name = rs.getString(2);
                expression.keywords = rs.getString(3);
                expression.content = rs.getString(4);
                return expression;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get expression", e);
            throw new DataException("failed to get expression");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Expression> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from expressions;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Expression> expressions = new ArrayList<Expression>();
            while(rs.next()) {
                Expression expression = new Expression();
                expression.id = rs.getInt(1);
                expression.name = rs.getString(2);
                expression.keywords = rs.getString(3);
                expression.content = rs.getString(4);
                expressions.add(expression);
            }
            return expressions;
        }
        catch(Exception e) {
            logger.error("failed to get expressions", e);
            throw new DataException("failed to get expressions");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Expression> query(QueryTemplate<Expression> template, Object... args) {
        return new ArrayList<Expression>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Expression expression) {
        return expression.id;
    }

    @Override
    public StringBuffer exportSingle(Expression t) {
        StringBuffer sb = new StringBuffer("INSERT INTO expressions (id, name, keywords, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.keywords));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
