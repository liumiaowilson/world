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
import org.wilson.world.model.NovelFragment;

import com.mysql.jdbc.Statement;

public class NovelFragmentDAO extends AbstractDAO<NovelFragment> {
    public static final String ITEM_TABLE_NAME = "novel_fragments";
    
    private static final Logger logger = Logger.getLogger(NovelFragmentDAO.class);

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public boolean isLoaded(NovelFragment t) {
        return t.content != null;
    }

    @Override
    public NovelFragment load(NovelFragment t) {
        return super.load(t);
    }

    @Override
    public NovelFragment unload(NovelFragment t) {
        t.content = null;
        return t;
    }
    
    @Override
    public void create(NovelFragment fragment) {
        if(fragment == null) {
            throw new DataException("NovelFragment should not be null");
        }
        if(StringUtils.isBlank(fragment.name)) {
            throw new DataException("NovelFragment should have a valid name");
        }
        if(StringUtils.isBlank(fragment.content)) {
            throw new DataException("NovelFragment should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into novel_fragments(name, stage_id, cond, content, pre_code, post_code, image) values (?, ?, ?, ?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fragment.name);
            ps.setInt(2, fragment.stageId);
            ps.setString(3, fragment.condition);
            ps.setString(4, fragment.content);
            ps.setString(5, fragment.preCode);
            ps.setString(6, fragment.postCode);
            ps.setString(7, fragment.image);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                fragment.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create novel fragment", e);
            throw new DataException("failed to create novel fragment");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(NovelFragment fragment) {
        if(fragment == null) {
            throw new DataException("NovelFragment should not be null");
        }
        if(StringUtils.isBlank(fragment.name)) {
            throw new DataException("NovelFragment should have a valid name");
        }
        if(StringUtils.isBlank(fragment.content)) {
            throw new DataException("NovelFragment should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update novel_fragments set name = ?, stage_id = ?, cond = ?, content = ?, pre_code = ?, post_code = ?, image = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, fragment.name);
            ps.setInt(2, fragment.stageId);
            ps.setString(3, fragment.condition);
            ps.setString(4, fragment.content);
            ps.setString(5, fragment.preCode);
            ps.setString(6, fragment.postCode);
            ps.setString(7, fragment.image);
            ps.setInt(8, fragment.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update novel fragment", e);
            throw new DataException("failed to update novel fragment");
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
            String sql = "delete from novel_fragments where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete novel fragment", e);
            throw new DataException("failed to delete novel fragment");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public NovelFragment get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_fragments where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	NovelFragment fragment = new NovelFragment();
                fragment.id = id;
                fragment.name = rs.getString(2);
                fragment.stageId = rs.getInt(3);
                fragment.condition = rs.getString(4);
                if(!lazy) {
                	fragment.content = rs.getString(5);
                }
                fragment.preCode = rs.getString(6);
                fragment.postCode = rs.getString(7);
                fragment.image = rs.getString(8);
                return fragment;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get novel fragment", e);
            throw new DataException("failed to get novel fragment");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelFragment> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from novel_fragments;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<NovelFragment> fragments = new ArrayList<NovelFragment>();
            while(rs.next()) {
            	NovelFragment fragment = new NovelFragment();
                fragment.id = rs.getInt(1);
                fragment.name = rs.getString(2);
                fragment.stageId = rs.getInt(3);
                fragment.condition = rs.getString(4);
                if(!lazy) {
                	fragment.content = rs.getString(5);
                }
                fragment.preCode = rs.getString(6);
                fragment.postCode = rs.getString(7);
                fragment.image = rs.getString(8);
                fragments.add(fragment);
            }
            return fragments;
        }
        catch(Exception e) {
            logger.error("failed to get novel fragments", e);
            throw new DataException("failed to get novel fragments");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<NovelFragment> query(QueryTemplate<NovelFragment> template, Object... args) {
        return new ArrayList<NovelFragment>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(NovelFragment fragment) {
        return fragment.id;
    }

    @Override
    public StringBuffer exportSingle(NovelFragment t) {
        StringBuffer sb = new StringBuffer("INSERT INTO novel_fragments (id, name, stage_id, cond, content, pre_code, post_code, image) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(t.stageId);
        sb.append(",");
        sb.append(escapeStr(t.condition));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(",");
        sb.append(escapeStr(t.preCode));
        sb.append(",");
        sb.append(escapeStr(t.postCode));
        sb.append(",");
        sb.append(escapeStr(t.image));
        sb.append(");");
        return sb;
    }

}
