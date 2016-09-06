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
import org.wilson.world.model.Artifact;

import com.mysql.jdbc.Statement;

public class ArtifactDAO extends AbstractDAO<Artifact> {
    public static final String ITEM_TABLE_NAME = "artifacts";
    
    private static final Logger logger = Logger.getLogger(ArtifactDAO.class);

    @Override
    public void create(Artifact artifact) {
        if(artifact == null) {
            throw new DataException("Artifact should not be null");
        }
        if(StringUtils.isBlank(artifact.name)) {
            throw new DataException("Artifact should have a valid name");
        }
        if(StringUtils.isBlank(artifact.content)) {
            throw new DataException("Artifact should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into artifacts(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, artifact.name);
            ps.setString(2, artifact.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                artifact.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create artifact", e);
            throw new DataException("failed to create artifact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Artifact artifact) {
        if(artifact == null) {
            throw new DataException("Artifact should not be null");
        }
        if(StringUtils.isBlank(artifact.name)) {
            throw new DataException("Artifact should have a valid name");
        }
        if(StringUtils.isBlank(artifact.content)) {
            throw new DataException("Artifact should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update artifacts set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, artifact.name);
            ps.setString(2, artifact.content);
            ps.setInt(3, artifact.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update artifact", e);
            throw new DataException("failed to update artifact");
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
            String sql = "delete from artifacts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete artifact", e);
            throw new DataException("failed to delete artifact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Artifact get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from artifacts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Artifact artifact = new Artifact();
                artifact.id = id;
                artifact.name = rs.getString(2);
                artifact.content = rs.getString(3);
                return artifact;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get artifact", e);
            throw new DataException("failed to get artifact");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Artifact> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from artifacts;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Artifact> artifacts = new ArrayList<Artifact>();
            while(rs.next()) {
                Artifact artifact = new Artifact();
                artifact.id = rs.getInt(1);
                artifact.name = rs.getString(2);
                artifact.content = rs.getString(3);
                artifacts.add(artifact);
            }
            return artifacts;
        }
        catch(Exception e) {
            logger.error("failed to get artifacts", e);
            throw new DataException("failed to get artifacts");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Artifact> query(QueryTemplate<Artifact> template, Object... args) {
        return new ArrayList<Artifact>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Artifact artifact) {
        return artifact.id;
    }

    @Override
    public StringBuffer exportSingle(Artifact t) {
        StringBuffer sb = new StringBuffer("INSERT INTO artifacts (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
