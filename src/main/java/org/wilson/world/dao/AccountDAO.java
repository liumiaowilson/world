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
import org.wilson.world.model.Account;

import com.mysql.jdbc.Statement;

public class AccountDAO extends AbstractDAO<Account> {
    public static final String ITEM_TABLE_NAME = "accounts";
    
    private static final Logger logger = Logger.getLogger(AccountDAO.class);

    @Override
    public void create(Account account) {
        if(account == null) {
            throw new DataException("account should not be null");
        }
        if(StringUtils.isBlank(account.name)) {
            throw new DataException("account should have a valid name");
        }
        if(StringUtils.isBlank(account.identifier)) {
            throw new DataException("account should have a valid identifier");
        }
        if(StringUtils.isBlank(account.description)) {
            throw new DataException("account should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "insert into accounts(name, identifier, description, amount) values (?, ?, ?, ?);";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.name);
            ps.setString(2, account.identifier);
            ps.setString(3, account.description);
            ps.setInt(4, account.amount);
            ps.execute();
            
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                int id = rs.getInt(1);
                account.id = id;
            }
        }
        catch(Exception e) {
            logger.error("failed to create account", e);
            throw new DataException("failed to create account");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public void update(Account account) {
        if(account == null) {
            throw new DataException("account should not be null");
        }
        if(StringUtils.isBlank(account.name)) {
            throw new DataException("account should have a valid name");
        }
        if(StringUtils.isBlank(account.identifier)) {
            throw new DataException("account should have a valid identifier");
        }
        if(StringUtils.isBlank(account.description)) {
            throw new DataException("account should have a valid description");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DBUtils.getConnection();
            String sql = "update accounts set name = ?, identifier = ?, description = ?, amount = ? where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, account.name);
            ps.setString(2, account.identifier);
            ps.setString(3, account.description);
            ps.setInt(4, account.amount);
            ps.setInt(5, account.id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to update account", e);
            throw new DataException("failed to update account");
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
            String sql = "delete from accounts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
        }
        catch(Exception e) {
            logger.error("failed to delete account", e);
            throw new DataException("failed to delete account");
        }
        finally {
            DBUtils.closeQuietly(con, ps, null);
        }
    }

    @Override
    public Account get(int id, boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from accounts where id = ?;";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()) {
                Account account = new Account();
                account.id = id;
                account.name = rs.getString(2);
                account.identifier = rs.getString(3);
                account.description = rs.getString(4);
                account.amount = rs.getInt(5);
                return account;
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            logger.error("failed to get account", e);
            throw new DataException("failed to get account");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Account> getAll(boolean lazy) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            String sql = "select * from accounts;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Account> accounts = new ArrayList<Account>();
            while(rs.next()) {
                Account account = new Account();
                account.id = rs.getInt(1);
                account.name = rs.getString(2);
                account.identifier = rs.getString(3);
                account.description = rs.getString(4);
                account.amount = rs.getInt(5);
                accounts.add(account);
            }
            return accounts;
        }
        catch(Exception e) {
            logger.error("failed to get accounts", e);
            throw new DataException("failed to get accounts");
        }
        finally {
            DBUtils.closeQuietly(con, ps, rs);
        }
    }

    @Override
    public List<Account> query(QueryTemplate<Account> template, Object... args) {
        return new ArrayList<Account>();
    }

    @Override
    public String getItemTableName() {
        return ITEM_TABLE_NAME;
    }

    @Override
    public int getId(Account account) {
        return account.id;
    }

    @Override
    public StringBuffer exportSingle(Account t) {
        StringBuffer sb = new StringBuffer("INSERT INTO accounts (id, name, identifier, description, amount) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.name));
        sb.append(",");
        sb.append(escapeStr(t.identifier));
        sb.append(",");
        sb.append(escapeStr(t.description));
        sb.append(",");
        sb.append(t.amount);
        sb.append(");");
        return sb;
    }

}
