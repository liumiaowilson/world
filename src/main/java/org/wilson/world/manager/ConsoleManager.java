package org.wilson.world.manager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.QueryResult;
import org.wilson.world.model.QueryRow;

public class ConsoleManager {
    private static final Logger logger = Logger.getLogger(ConsoleManager.class);
    
    private static ConsoleManager instance;
    
    private ConsoleManager() {}
    
    public static ConsoleManager getInstance() {
        if(instance == null) {
            instance = new ConsoleManager();
        }
        return instance;
    }
    
    public String run(String cmd) {
        if(StringUtils.isBlank(cmd)) {
            return null;
        }
        
        Scanner s = null;
        try {
            InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
            s = new Scanner(is);
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        catch(Exception e) {
            logger.error("failed to run command!", e);
            return e.getMessage();
        }
        finally {
            if(s != null) {
                s.close();
            }
        }
    }
    
    public QueryResult execute(String sql) {
        if(StringUtils.isBlank(sql)) {
            return null;
        }
        
        QueryResult result = new QueryResult();
        Connection con = DBUtils.getConnection();
        ResultSet rs = null;
        try {
            Statement s = con.createStatement();
            boolean flag = s.execute(sql);
            if(flag) {
                rs = s.getResultSet();
                result.rows = new ArrayList<QueryRow>();
                
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                QueryRow heading = new QueryRow();
                heading.items = new ArrayList<String>(count);
                for(int i = 1; i <= count; i++) {
                    String col_name = md.getColumnLabel(i);
                    heading.items.add(col_name);
                }
                result.heading = heading;
                
                while(rs.next()) {
                    QueryRow row = new QueryRow();
                    row.items = new ArrayList<String>(count);
                    for(int i = 1; i <= count; i++) {
                        String item = rs.getString(i);
                        row.items.add(item);
                    }
                    result.rows.add(row);
                }
                result.isQuery = true;
            }
            else {
                result.updateCount = s.getUpdateCount();
                result.isQuery = false;
            }
        }
        catch(Exception e) {
            logger.error("failed to execute sql!", e);
            throw new DataException("failed to execute sql!");
        }
        finally {
            DBUtils.closeQuietly(con, rs);
        }
        
        return result;
    }
}
