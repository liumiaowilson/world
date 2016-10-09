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
import org.wilson.world.model.Interview;

import com.mysql.jdbc.Statement;

public class InterviewDAO extends AbstractDAO<Interview> {
    public static final String ITEM_TABLE_NAME = "interviews";
    
    private static final Logger logger = Logger.getLogger(InterviewDAO.class);

    @Override
    public void create(Interview interview) {
        if(interview == null) {
            throw new DataException("Interview should not be null");
        }
        if(StringUtils.isBlank(interview.name)) {
            throw new DataException("Interview should have a valid name");
        }
        if(StringUtils.isBlank(interview.question)) {
            throw new DataException("Interview should have a valid question");
        }
        if(StringUtils.isBlank(interview.answer)) {
            throw new DataException("Interview should have a valid answer");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into interviews(name, question, answer) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, interview.name);
            ps.setString(2, interview.question);
            ps.setString(3, interview.answer);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                interview.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create interview", e);
            throw new DataException("failed to create interview");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Interview interview) {
        if(interview == null) {
            throw new DataException("Interview should not be null");
        }
        if(StringUtils.isBlank(interview.name)) {
            throw new DataException("Interview should have a valid name");
        }
        if(StringUtils.isBlank(interview.question)) {
            throw new DataException("Interview should have a valid question");
        }
        if(StringUtils.isBlank(interview.answer)) {
            throw new DataException("Interview should have a valid answer");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update interviews set name = ?, question = ?, answer = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, interview.name);
            ps.setString(2, interview.question);
            ps.setString(3, interview.answer);
            ps.setInt(4, interview.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update interview", e);
            throw new DataException("failed to update interview");
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
            String sql = "delete from interviews where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete interview", e);
            throw new DataException("failed to delete interview");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Interview get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from interviews where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Interview interview = new Interview();
                interview.id = id;
                interview.name = rs.getString(2);
                interview.question = rs.getString(3);
                interview.answer = rs.getString(4);
                return interview;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get interview", e);
            throw new DataException("failed to get interview");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Interview> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from interviews;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Interview> interviews = new ArrayList<Interview>();
            while(rs.next()) {
                Interview interview = new Interview();
                interview.id = rs.getInt(1);
                interview.name = rs.getString(2);
                interview.question = rs.getString(3);
                interview.answer = rs.getString(4);
                interviews.add(interview);
            }
            return interviews;
        }
        catch(Exception e) {
            logger.error("failed to get interviews", e);
            throw new DataException("failed to get interviews");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Interview> query(QueryTemplate<Interview> template, Object... args) {
        return new ArrayList<Interview>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Interview interview) {
        return interview.id;
    }

    @Override
    public StringBuffer exportSingle(Interview t) {
        StringBuffer sb = new StringBuffer("INSERT INTO interviews (id, name, question, answer) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.question));
        sb.append(",");
        sb.append(escapeStr(t.answer));
        sb.append(");");
        return sb;
    }

}
