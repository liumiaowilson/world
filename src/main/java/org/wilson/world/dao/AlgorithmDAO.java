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
import org.wilson.world.model.Algorithm;

import com.mysql.jdbc.Statement;

public class AlgorithmDAO extends AbstractDAO<Algorithm> {
    public static final String ITEM_TABLE_NAME = "algorithms";
    
    private static final Logger logger = Logger.getLogger(AlgorithmDAO.class);

    @Override
    public void create(Algorithm algorithm) {
        if(algorithm == null) {
            throw new DataException("algorithm should not be null");
        }
        if(StringUtils.isBlank(algorithm.name)) {
            throw new DataException("algorithm should have a valid name");
        }
        if(StringUtils.isBlank(algorithm.description)) {
            throw new DataException("algorithm should have a valid description");
        }
        if(StringUtils.isBlank(algorithm.impl)) {
            throw new DataException("algorithm should have a valid impl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into algorithms(name, problem_id, description, impl) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, algorithm.name);
            ps.setInt(2, algorithm.problemId);
            ps.setString(3, algorithm.description);
            ps.setString(4, algorithm.impl);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                algorithm.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create algorithm", e);
            throw new DataException("failed to create algorithm");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Algorithm algorithm) {
        if(algorithm == null) {
            throw new DataException("algorithm should not be null");
        }
        if(StringUtils.isBlank(algorithm.name)) {
            throw new DataException("algorithm should have a valid name");
        }
        if(StringUtils.isBlank(algorithm.description)) {
            throw new DataException("algorithm should have a valid description");
        }
        if(StringUtils.isBlank(algorithm.impl)) {
            throw new DataException("algorithm should have a valid impl");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update algorithms set name = ?, problem_id = ?, description = ?, impl = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, algorithm.name);
            ps.setInt(2, algorithm.problemId);
            ps.setString(3, algorithm.description);
            ps.setString(4, algorithm.impl);
            ps.setInt(5, algorithm.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update algorithm", e);
            throw new DataException("failed to update algorithm");
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
            String sql = "delete from algorithms where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete algorithm", e);
            throw new DataException("failed to delete algorithm");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Algorithm get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from algorithms where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Algorithm algorithm = new Algorithm();
                algorithm.id = id;
                algorithm.name = rs.getString(2);
                algorithm.problemId = rs.getInt(3);
                algorithm.description = rs.getString(4);
                algorithm.impl = rs.getString(5);
                return algorithm;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get algorithm", e);
            throw new DataException("failed to get algorithm");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Algorithm> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from algorithms;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Algorithm> algorithms = new ArrayList<Algorithm>();
            while(rs.next()) {
                Algorithm algorithm = new Algorithm();
                algorithm.id = rs.getInt(1);
                algorithm.name = rs.getString(2);
                algorithm.problemId = rs.getInt(3);
                algorithm.description = rs.getString(4);
                algorithm.impl = rs.getString(5);
                algorithms.add(algorithm);
            }
            return algorithms;
        }
        catch(Exception e) {
            logger.error("failed to get algorithms", e);
            throw new DataException("failed to get algorithms");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Algorithm> query(QueryTemplate<Algorithm> template, Object... args) {
        return new ArrayList<Algorithm>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Algorithm algorithm) {
        return algorithm.id;
    }

    @Override
    public StringBuffer exportSingle(Algorithm t) {
        StringBuffer sb = new StringBuffer("INSERT INTO algorithms (id, name, problem_id, description, impl) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(t.problemId);
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.impl));
        sb.append(");");
        return sb;
    }

}
