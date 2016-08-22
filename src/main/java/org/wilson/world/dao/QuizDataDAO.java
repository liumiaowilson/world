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
import org.wilson.world.model.QuizData;

import com.mysql.jdbc.Statement;

public class QuizDataDAO extends AbstractDAO<QuizData> {
    public static final String ITEM_TABLE_NAME = "quiz_datas";
    
    private static final Logger logger = Logger.getLogger(QuizDataDAO.class);

    @Override
    public void create(QuizData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.processor)) {
            throw new DataException("data should have a valid processor");
        }
        if(StringUtils.isBlank(data.content)) {
            throw new DataException("data should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into quiz_datas(name, description, processor, content) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.processor);
            ps.setString(4, data.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                data.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create data", e);
            throw new DataException("failed to create data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(QuizData data) {
        if(data == null) {
            throw new DataException("data should not be null");
        }
        if(StringUtils.isBlank(data.name)) {
            throw new DataException("data should have a valid name");
        }
        if(StringUtils.isBlank(data.description)) {
            throw new DataException("data should have a valid description");
        }
        if(StringUtils.isBlank(data.processor)) {
            throw new DataException("data should have a valid processor");
        }
        if(StringUtils.isBlank(data.content)) {
            throw new DataException("data should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update quiz_datas set name = ?, description = ?, processor = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, data.name);
            ps.setString(2, data.description);
            ps.setString(3, data.processor);
            ps.setString(4, data.content);
            ps.setInt(5, data.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update data", e);
            throw new DataException("failed to update data");
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
            String sql = "delete from quiz_datas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete data", e);
            throw new DataException("failed to delete data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public QuizData get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quiz_datas where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                QuizData data = new QuizData();
                data.id = id;
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.processor = rs.getString(4);
                data.content = rs.getString(5);
                return data;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get data", e);
            throw new DataException("failed to get data");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<QuizData> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from quiz_datas;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<QuizData> datas = new ArrayList<QuizData>();
            while(rs.next()) {
                QuizData data = new QuizData();
                data.id = rs.getInt(1);
                data.name = rs.getString(2);
                data.description = rs.getString(3);
                data.processor = rs.getString(4);
                data.content = rs.getString(5);
                datas.add(data);
            }
            return datas;
        }
        catch(Exception e) {
            logger.error("failed to get datas", e);
            throw new DataException("failed to get datas");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<QuizData> query(QueryTemplate<QuizData> template, Object... args) {
        return new ArrayList<QuizData>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(QuizData data) {
        return data.id;
    }

    @Override
    public StringBuffer exportSingle(QuizData t) {
        StringBuffer sb = new StringBuffer("INSERT INTO quiz_datas (id, name, description, processor, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(escapeStr(t.processor));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
