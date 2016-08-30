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
import org.wilson.world.model.AlgorithmProblem;

import com.mysql.jdbc.Statement;

public class AlgorithmProblemDAO extends AbstractDAO<AlgorithmProblem> {
    public static final String ITEM_TABLE_NAME = "algorithm_problems";
    
    private static final Logger logger = Logger.getLogger(AlgorithmProblemDAO.class);

    @Override
    public void create(AlgorithmProblem problem) {
        if(problem == null) {
            throw new DataException("problem should not be null");
        }
        if(StringUtils.isBlank(problem.name)) {
            throw new DataException("problem should have a valid name");
        }
        if(StringUtils.isBlank(problem.description)) {
            throw new DataException("problem should have a valid description");
        }
        if(StringUtils.isBlank(problem.interfaceDef)) {
            throw new DataException("problem should have a valid interfaceDef");
        }
        if(StringUtils.isBlank(problem.dataset)) {
            throw new DataException("problem should have a valid dataset");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into algorithm_problems(name, description, interface, dataset) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, problem.name);
            ps.setString(2, problem.description);
            ps.setString(3, problem.interfaceDef);
            ps.setString(4, problem.dataset);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                problem.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create problem", e);
            throw new DataException("failed to create problem");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(AlgorithmProblem problem) {
        if(problem == null) {
            throw new DataException("problem should not be null");
        }
        if(StringUtils.isBlank(problem.name)) {
            throw new DataException("problem should have a valid name");
        }
        if(StringUtils.isBlank(problem.description)) {
            throw new DataException("problem should have a valid description");
        }
        if(StringUtils.isBlank(problem.interfaceDef)) {
            throw new DataException("problem should have a valid interfaceDef");
        }
        if(StringUtils.isBlank(problem.dataset)) {
            throw new DataException("problem should have a valid dataset");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update algorithm_problems set name = ?, description = ?, interface = ?, dataset = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, problem.name);
            ps.setString(2, problem.description);
            ps.setString(3, problem.interfaceDef);
            ps.setString(4, problem.dataset);
            ps.setInt(5, problem.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update problem", e);
            throw new DataException("failed to update problem");
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
            String sql = "delete from algorithm_problems where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete problem", e);
            throw new DataException("failed to delete problem");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public AlgorithmProblem get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from algorithm_problems where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                AlgorithmProblem problem = new AlgorithmProblem();
                problem.id = id;
                problem.name = rs.getString(2);
                problem.description = rs.getString(3);
                problem.interfaceDef = rs.getString(4);
                problem.dataset = rs.getString(5);
                return problem;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get problem", e);
            throw new DataException("failed to get problem");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<AlgorithmProblem> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from algorithm_problems;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<AlgorithmProblem> problems = new ArrayList<AlgorithmProblem>();
            while(rs.next()) {
                AlgorithmProblem problem = new AlgorithmProblem();
                problem.id = rs.getInt(1);
                problem.name = rs.getString(2);
                problem.description = rs.getString(3);
                problem.interfaceDef = rs.getString(4);
                problem.dataset = rs.getString(5);
                problems.add(problem);
            }
            return problems;
        }
        catch(Exception e) {
            logger.error("failed to get problems", e);
            throw new DataException("failed to get problems");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<AlgorithmProblem> query(QueryTemplate<AlgorithmProblem> template, Object... args) {
        return new ArrayList<AlgorithmProblem>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(AlgorithmProblem problem) {
        return problem.id;
    }

    @Override
    public StringBuffer exportSingle(AlgorithmProblem t) {
        StringBuffer sb = new StringBuffer("INSERT INTO algorithm_problems (id, name, description, interface, dataset) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.interfaceDef));
        sb.append(",");
        sb.append(escapeStr(t.dataset));
        sb.append(");");
        return sb;
    }

}
