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
import org.wilson.world.model.CodeTemplate;

import com.mysql.jdbc.Statement;

public class CodeTemplateDAO extends AbstractDAO<CodeTemplate> {
    public static final String ITEM_TABLE_NAME = "code_templates";
    
    private static final Logger logger = Logger.getLogger(CodeTemplateDAO.class);

    @Override
    public void create(CodeTemplate template) {
        if(template == null) {
            throw new DataException("CodeTemplate should not be null");
        }
        if(StringUtils.isBlank(template.name)) {
            throw new DataException("CodeTemplate should have a valid name");
        }
        if(StringUtils.isBlank(template.content)) {
            throw new DataException("CodeTemplate should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into code_templates(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, template.name);
            ps.setString(2, template.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                template.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create code template", e);
            throw new DataException("failed to create code template");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(CodeTemplate template) {
        if(template == null) {
            throw new DataException("CodeTemplate should not be null");
        }
        if(StringUtils.isBlank(template.name)) {
            throw new DataException("CodeTemplate should have a valid name");
        }
        if(StringUtils.isBlank(template.content)) {
            throw new DataException("CodeTemplate should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update code_templates set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, template.name);
            ps.setString(2, template.content);
            ps.setInt(3, template.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update code template", e);
            throw new DataException("failed to update code template");
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
            String sql = "delete from code_templates where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete code template", e);
            throw new DataException("failed to delete code template");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public CodeTemplate get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_templates where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	CodeTemplate template = new CodeTemplate();
                template.id = id;
                template.name = rs.getString(2);
                template.content = rs.getString(3);
                return template;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get code template", e);
            throw new DataException("failed to get code template");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeTemplate> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_templates;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<CodeTemplate> templates = new ArrayList<CodeTemplate>();
            while(rs.next()) {
            	CodeTemplate template = new CodeTemplate();
                template.id = rs.getInt(1);
                template.name = rs.getString(2);
                template.content = rs.getString(3);
                templates.add(template);
            }
            return templates;
        }
        catch(Exception e) {
            logger.error("failed to get code templates", e);
            throw new DataException("failed to get code templates");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeTemplate> query(QueryTemplate<CodeTemplate> template, Object... args) {
        return new ArrayList<CodeTemplate>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(CodeTemplate template) {
        return template.id;
    }

    @Override
    public StringBuffer exportSingle(CodeTemplate t) {
        StringBuffer sb = new StringBuffer("INSERT INTO code_templates (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
