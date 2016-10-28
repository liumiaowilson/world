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
import org.wilson.world.model.CodeSnippet;

import com.mysql.jdbc.Statement;

public class CodeSnippetDAO extends AbstractDAO<CodeSnippet> {
    public static final String ITEM_TABLE_NAME = "code_snippets";
    
    private static final Logger logger = Logger.getLogger(CodeSnippetDAO.class);

    @Override
    public void create(CodeSnippet snippet) {
        if(snippet == null) {
            throw new DataException("CodeSnippet should not be null");
        }
        if(StringUtils.isBlank(snippet.content)) {
            throw new DataException("CodeSnippet should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into code_snippets(language_id, template_id, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, snippet.languageId);
            ps.setInt(2, snippet.templateId);
            ps.setString(3, snippet.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                snippet.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create code snippet", e);
            throw new DataException("failed to create code snippet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(CodeSnippet snippet) {
        if(snippet == null) {
            throw new DataException("CodeSnippet should not be null");
        }
        if(StringUtils.isBlank(snippet.content)) {
            throw new DataException("CodeSnippet should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update code_snippets set language_id = ?, template_id = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, snippet.languageId);
            ps.setInt(2, snippet.templateId);
            ps.setString(3, snippet.content);
            ps.setInt(4, snippet.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update code snippet", e);
            throw new DataException("failed to update code snippet");
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
            String sql = "delete from code_snippets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete code snippet", e);
            throw new DataException("failed to delete code snippet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public CodeSnippet get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_snippets where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	CodeSnippet snippet = new CodeSnippet();
                snippet.id = id;
                snippet.languageId = rs.getInt(2);
                snippet.templateId = rs.getInt(3);
                snippet.content = rs.getString(4);
                return snippet;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get code snippet", e);
            throw new DataException("failed to get code snippet");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeSnippet> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_snippets;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<CodeSnippet> snippets = new ArrayList<CodeSnippet>();
            while(rs.next()) {
            	CodeSnippet snippet = new CodeSnippet();
                snippet.id = rs.getInt(1);
                snippet.languageId = rs.getInt(2);
                snippet.templateId = rs.getInt(3);
                snippet.content = rs.getString(4);
                snippets.add(snippet);
            }
            return snippets;
        }
        catch(Exception e) {
            logger.error("failed to get code snippets", e);
            throw new DataException("failed to get code snippets");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeSnippet> query(QueryTemplate<CodeSnippet> template, Object... args) {
        return new ArrayList<CodeSnippet>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(CodeSnippet snippet) {
        return snippet.id;
    }

    @Override
    public StringBuffer exportSingle(CodeSnippet t) {
        StringBuffer sb = new StringBuffer("INSERT INTO code_snippets (id, language_id, template_id, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(t.languageId);
        sb.append(",");
        sb.append(t.templateId);
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
