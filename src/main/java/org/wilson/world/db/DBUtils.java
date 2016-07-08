package org.wilson.world.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DBUtils {
    private static final Logger logger = Logger.getLogger(DBUtils.class);
    private static DataSource datasource;
    
    public static Connection getConnection() throws Exception{
        return getDataSource().getConnection();
    }
    
    public static DataSource getDataSource() {
        if(datasource == null) {
            datasource = loadDataSource();
        }
        return datasource;
    }
    
    private static DataSource loadDataSource() {
        try {
            InitialContext ic = new InitialContext();
            Context initialContext = (Context) ic.lookup("java:comp/env");
            DataSource datasource = (DataSource) initialContext.lookup("jdbc/MySQLDS");
            return datasource;
        }
        catch(Exception e) {
            logger.error("failed to load datasource!", e);
            return null;
        }
    }
    
    public static void closeQuietly(Connection con, Statement s, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        
        if(s != null) {
            try {
                s.close();
            }
            catch(SQLException e) {
            }
        }
        
        if(con != null) {
            try {
                con.close();
            }
            catch(SQLException e) {
            }
        }
    }
}
