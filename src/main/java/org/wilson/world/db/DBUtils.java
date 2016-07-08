package org.wilson.world.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DBUtils {
    private static final Logger logger = Logger.getLogger(DBUtils.class);
    private static Connection connection;
    
    public static Connection getConnection() {
        if(connection == null) {
            connection = loadConnection();
        }
        return connection;
    }
    
    private static Connection loadConnection() {
        try {
            InitialContext ic = new InitialContext();
            Context initialContext = (Context) ic.lookup("java:comp/env");
            DataSource datasource = (DataSource) initialContext.lookup("jdbc/MySQLDS");
            Connection result = datasource.getConnection();
            return result;
        }
        catch(Exception e) {
            logger.error("failed to load jdbc connection!", e);
            return null;
        }
    }
    
    public static void closeQuietly(Connection con, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        
        if(con != null) {
            //skip closing pooled connection
        }
    }
}
