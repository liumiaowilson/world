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
import org.wilson.world.model.CodeLanguage;

import com.mysql.jdbc.Statement;

public class CodeLanguageDAO extends AbstractDAO<CodeLanguage> {
    public static final String ITEM_TABLE_NAME = "code_languages";
    
    private static final Logger logger = Logger.getLogger(CodeLanguageDAO.class);

    @Override
    public void create(CodeLanguage lang) {
        if(lang == null) {
            throw new DataException("CodeLanguage should not be null");
        }
        if(StringUtils.isBlank(lang.name)) {
            throw new DataException("CodeLanguage should have a valid name");
        }
        if(StringUtils.isBlank(lang.type)) {
            throw new DataException("CodeLanguage should have a valid type");
        }
        if(StringUtils.isBlank(lang.content)) {
            throw new DataException("CodeLanguage should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into code_languages(name, type, content) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, lang.name);
            ps.setString(2, lang.type);
            ps.setString(3, lang.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                lang.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create code language", e);
            throw new DataException("failed to create code language");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(CodeLanguage lang) {
    	if(lang == null) {
            throw new DataException("CodeLanguage should not be null");
        }
        if(StringUtils.isBlank(lang.name)) {
            throw new DataException("CodeLanguage should have a valid name");
        }
        if(StringUtils.isBlank(lang.type)) {
            throw new DataException("CodeLanguage should have a valid type");
        }
        if(StringUtils.isBlank(lang.content)) {
            throw new DataException("CodeLanguage should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update code_languages set name = ?, type = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, lang.name);
            ps.setString(2, lang.type);
            ps.setString(3, lang.content);
            ps.setInt(4, lang.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update code language", e);
            throw new DataException("failed to update code language");
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
            String sql = "delete from code_languages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete code language", e);
            throw new DataException("failed to delete code language");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public CodeLanguage get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_languages where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
            	CodeLanguage lang = new CodeLanguage();
                lang.id = id;
                lang.name = rs.getString(2);
                lang.type = rs.getString(3);
                lang.content = rs.getString(4);
                return lang;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get code language", e);
            throw new DataException("failed to get code language");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeLanguage> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from code_languages;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<CodeLanguage> langs = new ArrayList<CodeLanguage>();
            while(rs.next()) {
            	CodeLanguage lang = new CodeLanguage();
                lang.id = rs.getInt(1);
                lang.name = rs.getString(2);
                lang.type = rs.getString(3);
                lang.content = rs.getString(4);
                langs.add(lang);
            }
            return langs;
        }
        catch(Exception e) {
            logger.error("failed to get code languages", e);
            throw new DataException("failed to get code languages");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<CodeLanguage> query(QueryTemplate<CodeLanguage> template, Object... args) {
        return new ArrayList<CodeLanguage>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(CodeLanguage lang) {
        return lang.id;
    }

    @Override
    public StringBuffer exportSingle(CodeLanguage t) {
        StringBuffer sb = new StringBuffer("INSERT INTO code_languages (id, name, type, content) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.type));
        sb.append(",");
        sb.append(escapeStr(t.content));
        sb.append(");");
        return sb;
    }

}
