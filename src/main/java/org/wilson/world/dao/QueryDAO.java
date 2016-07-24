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
import org.wilson.world.model.Query;

import com.mysql.jdbc.Statement;

public class QueryDAO extends AbstractDAO<Query> {
    public static final String ITEM_TABLE_NAME = "queries";
    
    private static final Logger logger = Logger.getLogger(QueryDAO.class);

    @Override
    public void create(Query query) {
        if(query == null) {
            throw new DataException("query should not be null");
        }
        if(StringUtils.isBlank(query.name)) {
            throw new DataException("query should have a valid name");
        }
        if(StringUtils.isBlank(query.impl)) {
            throw new DataException("query should have a valid impl");
        }
        if(StringUtils.isBlank(query.idExpr)) {
            throw new DataException("query should have a valid id expression");
        }
        if(StringUtils.isBlank(query.nameExpr)) {
            throw new DataException("query should have a valid name expression");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into queries(name, impl, id_expr, name_expr) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, query.name);
            ps.setString(2, query.impl);
            ps.setString(3, query.idExpr);
            ps.setString(4, query.nameExpr);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                query.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create query", e);
            throw new DataException("failed to create query");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Query query) {
        if(query == null) {
            throw new DataException("query should not be null");
        }
        if(StringUtils.isBlank(query.name)) {
            throw new DataException("query should have a valid name");
        }
        if(StringUtils.isBlank(query.impl)) {
            throw new DataException("query should have a valid impl");
        }
        if(StringUtils.isBlank(query.idExpr)) {
            throw new DataException("query should have a valid id expression");
        }
        if(StringUtils.isBlank(query.nameExpr)) {
            throw new DataException("query should have a valid name expression");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update queries set name = ?, impl = ?, id_expr = ?, name_expr = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, query.name);
            ps.setString(2, query.impl);
            ps.setString(3, query.idExpr);
            ps.setString(4, query.nameExpr);
            ps.setInt(5, query.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update query", e);
            throw new DataException("failed to update query");
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
            String sql = "delete from queries where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete query", e);
            throw new DataException("failed to delete query");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Query get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from queries where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Query query = new Query();
                query.id = id;
                query.name = rs.getString(2);
                query.impl = rs.getString(3);
                query.idExpr = rs.getString(4);
                query.nameExpr = rs.getString(5);
                return query;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get query", e);
            throw new DataException("failed to get query");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Query> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from queries;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Query> queries = new ArrayList<Query>();
            while(rs.next()) {
                Query query = new Query();
                query.id = rs.getInt(1);
                query.name = rs.getString(2);
                query.impl = rs.getString(3);
                query.idExpr = rs.getString(4);
                query.nameExpr = rs.getString(5);
                queries.add(query);
            }
            return queries;
        }
        catch(Exception e) {
            logger.error("failed to get queries", e);
            throw new DataException("failed to get queries");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Query> query(QueryTemplate<Query> template, Object... args) {
        return new ArrayList<Query>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Query query) {
        return query.id;
    }

    @Override
    public StringBuffer exportSingle(Query t) {
        StringBuffer sb = new StringBuffer("INSERT INTO queries (id, name, impl, id_expr, name_expr) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.impl));
        sb.append("','");
        sb.append(escape(t.idExpr));
        sb.append("','");
        sb.append(escape(t.nameExpr));
        sb.append("');");
        return sb;
    }

}
