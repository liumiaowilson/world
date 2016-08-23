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
import org.wilson.world.model.Alias;

import com.mysql.jdbc.Statement;

public class AliasDAO extends AbstractDAO<Alias> {
    public static final String ITEM_TABLE_NAME = "aliases";
    
    private static final Logger logger = Logger.getLogger(AliasDAO.class);

    @Override
    public void create(Alias alias) {
        if(alias == null) {
            throw new DataException("alias should not be null");
        }
        if(StringUtils.isBlank(alias.name)) {
            throw new DataException("alias should have a valid name");
        }
        if(StringUtils.isBlank(alias.content)) {
            throw new DataException("alias should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into aliases(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, alias.name);
            ps.setString(2, alias.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                alias.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create alias", e);
            throw new DataException("failed to create alias");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Alias alias) {
        if(alias == null) {
            throw new DataException("alias should not be null");
        }
        if(StringUtils.isBlank(alias.name)) {
            throw new DataException("alias should have a valid name");
        }
        if(StringUtils.isBlank(alias.content)) {
            throw new DataException("alias should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update aliases set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, alias.name);
            ps.setString(2, alias.content);
            ps.setInt(3, alias.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update alias", e);
            throw new DataException("failed to update alias");
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
            String sql = "delete from aliases where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete alias", e);
            throw new DataException("failed to delete alias");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Alias get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from aliases where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Alias alias = new Alias();
                alias.id = id;
                alias.name = rs.getString(2);
                alias.content = rs.getString(3);
                return alias;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get alias", e);
            throw new DataException("failed to get alias");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Alias> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from aliases;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Alias> aliases = new ArrayList<Alias>();
            while(rs.next()) {
                Alias alias = new Alias();
                alias.id = rs.getInt(1);
                alias.name = rs.getString(2);
                alias.content = rs.getString(3);
                aliases.add(alias);
            }
            return aliases;
        }
        catch(Exception e) {
            logger.error("failed to get aliases", e);
            throw new DataException("failed to get aliases");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Alias> query(QueryTemplate<Alias> template, Object... args) {
        return new ArrayList<Alias>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Alias alias) {
        return alias.id;
    }

    @Override
    public StringBuffer exportSingle(Alias t) {
        StringBuffer sb = new StringBuffer("INSERT INTO aliases (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
