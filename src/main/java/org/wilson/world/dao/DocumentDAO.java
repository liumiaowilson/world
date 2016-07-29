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
import org.wilson.world.model.Document;

import com.mysql.jdbc.Statement;

public class DocumentDAO extends AbstractDAO<Document> {
    public static final String ITEM_TABLE_NAME = "documents";
    
    private static final Logger logger = Logger.getLogger(DocumentDAO.class);

    @Override
    public void create(Document document) {
        if(document == null) {
            throw new DataException("document should not be null");
        }
        if(StringUtils.isBlank(document.name)) {
            throw new DataException("document should have a valid name");
        }
        if(StringUtils.isBlank(document.content)) {
            throw new DataException("document should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into documents(name, content) values (?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, document.name);
            ps.setString(2, document.content);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                document.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create document", e);
            throw new DataException("failed to create document");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Document document) {
        if(document == null) {
            throw new DataException("document should not be null");
        }
        if(StringUtils.isBlank(document.name)) {
            throw new DataException("document should have a valid name");
        }
        if(StringUtils.isBlank(document.content)) {
            throw new DataException("document should have a valid content");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update documents set name = ?, content = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, document.name);
            ps.setString(2, document.content);
            ps.setInt(3, document.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update document", e);
            throw new DataException("failed to update document");
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
            String sql = "delete from documents where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete document", e);
            throw new DataException("failed to delete document");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Document get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from documents where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Document document = new Document();
                document.id = id;
                document.name = rs.getString(2);
                document.content = rs.getString(3);
                return document;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get document", e);
            throw new DataException("failed to get document");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Document> getAll() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from documents;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Document> documents = new ArrayList<Document>();
            while(rs.next()) {
                Document document = new Document();
                document.id = rs.getInt(1);
                document.name = rs.getString(2);
                document.content = rs.getString(3);
                documents.add(document);
            }
            return documents;
        }
        catch(Exception e) {
            logger.error("failed to get documents", e);
            throw new DataException("failed to get documents");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Document> query(QueryTemplate<Document> template, Object... args) {
        return new ArrayList<Document>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Document document) {
        return document.id;
    }

    @Override
    public StringBuffer exportSingle(Document t) {
        StringBuffer sb = new StringBuffer("INSERT INTO documents (id, name, content) VALUES (");
        sb.append(t.id);
        sb.append(",'");
        sb.append(escape(t.name));
        sb.append("','");
        sb.append(escape(t.content));
        sb.append("');");
        return sb;
    }

}
