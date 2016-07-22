package org.wilson.world.manager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.QueryResult;
import org.wilson.world.model.QueryRow;
import org.wilson.world.usage.StorageUsageMonitor;
import org.wilson.world.util.FormatUtils;

public class ConsoleManager {
    private static final Logger logger = Logger.getLogger(ConsoleManager.class);
    
    private static ConsoleManager instance;
    
    private List<String []> errors = new ArrayList<String []>();
    
    private ConsoleManager() {
        MonitorManager.getInstance().registerMonitorParticipant(new StorageUsageMonitor());
    }
    
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
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            con = DBUtils.getConnection();
            s = con.createStatement();
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
            DBUtils.closeQuietly(con, s, rs);
        }
        
        return result;
    }
    
    int [] analyzeStorageUsage(String result) {
        String [] lines = result.split("\n");
        String last_line = lines[lines.length - 1].trim();
        if(logger.isDebugEnabled()) {
            logger.debug("last line is " + last_line);
        }
        String [] items = last_line.split("\\s+");
        int [] ret = new int[2];
        
        String used = items[0];
        used = used.substring(0, used.length() - 1);
        ret[0] = Integer.parseInt(used);
        
        String max = items[2];
        max = max.substring(0, max.length() - 1);
        ret[1] = Integer.parseInt(max);
        
        return ret;
    }
    
    /**
     * Get % (used, free)
     * 
     * @return
     */
    public double [] getStorageUsageDisplay() {
        int [] usage = this.getStorageUsage();
        double [] ret = new double[2];
        int used = usage[0];
        int max = usage[1];
        double used_pt = used * 100.0 / max;
        double free_pt = (max - used) * 100.0 / max;
        used_pt = FormatUtils.getRoundedValue(used_pt);
        free_pt = FormatUtils.getRoundedValue(free_pt);
        ret[0] = used_pt;
        ret[1] = free_pt;
        
        return ret;
    }
    
    /**
     * Get (used, max)
     * 
     * @return
     */
    public int [] getStorageUsage() {
        if(ConfigManager.getInstance().isOpenShiftApp()) {
            String result = this.run("quota -s");
            if(logger.isDebugEnabled()) {
                logger.debug("get quota raw result : " + result);
            }
            return this.analyzeStorageUsage(result);
        }
        else {
            //fake value
            return new int[]{100, 1024};
        }
    }
    
    public int getNumOfExceedLimitHits() {
        if(ConfigManager.getInstance().isOpenShiftApp()) {
            String result = this.run("oo-cgroup-read memory.failcnt");
            int num = Integer.parseInt(result.trim());
            return num;
        }
        else {
            //fake value
            return 0;
        }
    }
    
    public int getNumOfOutOfMemoryHits() {
        if(ConfigManager.getInstance().isOpenShiftApp()) {
            String result = this.run("oo-cgroup-read memory.memsw.failcnt");
            int num = Integer.parseInt(result.trim());
            return num;
        }
        else {
            //fake value
            return 0;
        }
    }
    
    public String deleteLogs() {
        if(ConfigManager.getInstance().isOpenShiftApp()) {
            this.run("rm -rf ../app-root/logs/jbossews.log");
            this.run("rm -rf ../app-root/logs/mysql.log");
            this.run("rm -rf ../app-root/logs/phpmyadmin.log");
            return "Logs deleted successfully";
        }
        else {
            //fake value
            return null;
        }
    }
    
    public void releaseMemory() {
        System.gc();
    }
    
    public void addError(String [] errorTrace) {
        this.errors.add(errorTrace);
    }
    
    public List<String []> getErrors() {
        return this.errors;
    }
}
