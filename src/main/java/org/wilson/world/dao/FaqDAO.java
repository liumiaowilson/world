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
import org.wilson.world.model.Faq;

import com.mysql.jdbc.Statement;

public class FaqDAO extends AbstractDAO<Faq> {
    public static final String ITEM_TABLE_NAME = "faqs";
    
    private static final Logger logger = Logger.getLogger(FaqDAO.class);

    @Override
    public void create(Faq faq) {
        if(faq == null) {
            throw new DataException("faq should not be null");
        }
        if(StringUtils.isBlank(faq.name)) {
            throw new DataException("faq should have a valid name");
        }
        if(StringUtils.isBlank(faq.question)) {
            throw new DataException("fqa should have a valid question");
        }
        if(StringUtils.isBlank(faq.answer)) {
            throw new DataException("fqa should have a valid answer");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into faqs(name, question, answer) values (?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, faq.name);
            ps.setString(2, faq.question);
            ps.setString(3, faq.answer);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                faq.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create faq", e);
            throw new DataException("failed to create faq");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Faq faq) {
        if(faq == null) {
            throw new DataException("faq should not be null");
        }
        if(StringUtils.isBlank(faq.name)) {
            throw new DataException("faq should have a valid name");
        }
        if(StringUtils.isBlank(faq.question)) {
            throw new DataException("fqa should have a valid question");
        }
        if(StringUtils.isBlank(faq.answer)) {
            throw new DataException("fqa should have a valid answer");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update faqs set name = ?, question = ?, answer = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, faq.name);
            ps.setString(2, faq.question);
            ps.setString(3, faq.answer);
            ps.setInt(4, faq.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update faq", e);
            throw new DataException("failed to update faq");
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
            String sql = "delete from faqs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete faq", e);
            throw new DataException("failed to delete faq");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Faq get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from faqs where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Faq faq = new Faq();
                faq.id = id;
                faq.name = rs.getString(2);
                faq.question = rs.getString(3);
                faq.answer = rs.getString(4);
                return faq;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get faq", e);
            throw new DataException("failed to get faq");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Faq> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from faqs;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Faq> faqs = new ArrayList<Faq>();
            while(rs.next()) {
                Faq faq = new Faq();
                faq.id = rs.getInt(1);
                faq.name = rs.getString(2);
                faq.question = rs.getString(3);
                faq.answer = rs.getString(4);
                faqs.add(faq);
            }
            return faqs;
        }
        catch(Exception e) {
            logger.error("failed to get faqs", e);
            throw new DataException("failed to get faqs");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Faq> query(QueryTemplate<Faq> template, Object... args) {
        return new ArrayList<Faq>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Faq faq) {
        return faq.id;
    }

    @Override
    public StringBuffer exportSingle(Faq t) {
        StringBuffer sb = new StringBuffer("INSERT INTO faqs (id, name, question, answer) VALUES (");
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
