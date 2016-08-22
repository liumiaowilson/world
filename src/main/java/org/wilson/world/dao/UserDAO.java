package org.wilson.world.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.model.User;

public class UserDAO extends AbstractDAO<User> {
    public static final String TABLE_NAME = "users";
    
    private static final Logger logger = Logger.getLogger(UserDAO.class);

    @Override
    public void create(User t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(User t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User get(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getAll() {
        List<User> ret = new ArrayList<User>();
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
            String sql = "select * from users;";
            rs = s.executeQuery(sql);
            while(rs.next()) {
                String username = rs.getString(2);
                String password = rs.getString(3);
                User user = new User();
                user.id = rs.getInt(1);
                user.username = username;
                user.password = password;
                ret.add(user);
            }
        }
        catch(Exception e) {
            logger.error("failed to load users!", e);
        }
        finally {
            DBUtils.closeQuietly(con, s, rs);
        }
        return ret;
    }

    @Override
    public List<User> query(QueryTemplate<User> template, Object... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getItemTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getId(User user) {
        return user.id;
    }

    @Override
    public StringBuffer exportSingle(User t) {
        StringBuffer sb = new StringBuffer("INSERT INTO users (id, username, password) VALUES (");
        sb.append(t.id);
        sb.append(",");
        sb.append(escapeStr(t.username));
        sb.append(",");
        sb.append(escapeStr(t.password));
        sb.append(");");
        return sb;
    }

}
