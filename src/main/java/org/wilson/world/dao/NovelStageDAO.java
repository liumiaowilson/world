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
import org.wilson.world.model.NovelStage;

import com.mysql.jdbc.Statement;

public class NovelStageDAO extends AbstractDAO<NovelStage> {
    public static final String ITEM_TABLE_NAME = "novel_stages";
    
    private static final Logger logger = Logger.getLogger(NovelStageDAO.class);

    @Override
    public void create(NovelStage stage) {
        if(stage == null) {
            throw new DataException("NovelStage should not be null");
        }
        if(StringUtils.isBlank(stage.name)) {
            throw new DataException("NovelStage should have a valid name");
        }
        if(StringUtils.isBlank(stage.description)) {
            throw new DataException("NovelStage should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_stages(name, description, prev_id, status, image, cond, pre_code, post_code) values (?, ?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, stage.name);
            ps.setString(2, stage.description);
            ps.setInt(3, stage.previousId);
            ps.setString(4, stage.status);
            ps.setString(5, stage.image);
            ps.setString(6, stage.condition);
            ps.setString(7, stage.preCode);
            ps.setString(8, stage.postCode);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                stage.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel stage", e);
            throw new DataException("failed to create novel stage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelStage stage) {
        if(stage == null) {
            throw new DataException("NovelStage should not be null");
        }
        if(StringUtils.isBlank(stage.name)) {
            throw new DataException("NovelStage should have a valid name");
        }
        if(StringUtils.isBlank(stage.description)) {
            throw new DataException("NovelStage should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_stages set name = ?, description = ?, prev_id = ?, status = ?, image = ?, cond = ?, pre_code = ?, post_code = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, stage.name);
            ps.setString(2, stage.description);
            ps.setInt(3, stage.previousId);
            ps.setString(4, stage.status);
            ps.setString(5, stage.image);
            ps.setString(6, stage.condition);
            ps.setString(7, stage.preCode);
            ps.setString(8, stage.postCode);
            ps.setInt(9, stage.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel stage", e);
            throw new DataException("failed to update novel stage");
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
            String sql = "delete from novel_stages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel stage", e);
            throw new DataException("failed to delete novel stage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelStage get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_stages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelStage stage = new NovelStage();
                stage.id = id;
                stage.name = rs.getString(2);
                stage.description = rs.getString(3);
                stage.previousId = rs.getInt(4);
                stage.status = rs.getString(5);
                stage.image = rs.getString(6);
                stage.condition = rs.getString(7);
                stage.preCode = rs.getString(8);
                stage.postCode = rs.getString(9);
                return stage;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel stage", e);
            throw new DataException("failed to get novel stage");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelStage> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_stages;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelStage> stages = new ArrayList<NovelStage>();
            while(rs.next()) {
            	NovelStage stage = new NovelStage();
                stage.id = rs.getInt(1);
                stage.name = rs.getString(2);
                stage.description = rs.getString(3);
                stage.previousId = rs.getInt(4);
                stage.status = rs.getString(5);
                stage.image = rs.getString(6);
                stage.condition = rs.getString(7);
                stage.preCode = rs.getString(8);
                stage.postCode = rs.getString(9);
                stages.add(stage);
            }
            return stages;
        }
        catch(Exception e) {
            logger.error("failed to get novel stages", e);
            throw new DataException("failed to get novel stages");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelStage> query(QueryTemplate<NovelStage> template, Object... args) {
        return new ArrayList<NovelStage>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelStage stage) {
        return stage.id;
    }

    @Override
    public StringBuffer exportSingle(NovelStage t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_stages (id, name, description, prev_id, status, image, cond, pre_code, post_code) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(t.previousId);
        sb.append(",");
        sb.append(escapeStr(t.status));
        sb.append(",");
        sb.append(escapeStr(t.image));
        sb.append(",");
        sb.append(escapeStr(t.condition));
        sb.append(",");
        sb.append(escapeStr(t.preCode));
        sb.append(",");
        sb.append(escapeStr(t.postCode));
        sb.append(");");
        return sb;
    }

}
