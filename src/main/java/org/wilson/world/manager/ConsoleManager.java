package org.wilson.world.manager;

import java.io.File;
import java.io.InputStream;
import java.lang.management.MemoryUsage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.console.BackupWorldTaskGenerator;
import org.wilson.world.console.FileInfo;
import org.wilson.world.console.MemoryInfo;
import org.wilson.world.console.ObjectGraphInfo;
import org.wilson.world.console.ObjectGraphMeasurer;
import org.wilson.world.console.ObjectGraphMeasurer.Footprint;
import org.wilson.world.console.RequestInfo;
import org.wilson.world.console.ResponseTimeSection;
import org.wilson.world.db.DBUtils;
import org.wilson.world.exception.DataException;
import org.wilson.world.model.QueryResult;
import org.wilson.world.model.QueryRow;
import org.wilson.world.usage.MemoryUsageMonitor;
import org.wilson.world.usage.ReleaseMemJob;
import org.wilson.world.usage.StorageUsageMonitor;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.HeapDumper;
import org.wilson.world.util.SizeUtils;
import org.wilson.world.util.TimeUtils;

public class ConsoleManager {
    private static final Logger logger = Logger.getLogger(ConsoleManager.class);
    
    private static ConsoleManager instance;
    
    private long startedTime;
    
    private LinkedList<MemoryInfo> infos = new LinkedList<MemoryInfo>();
    
    private LinkedList<RequestInfo> requests = new LinkedList<RequestInfo>();
    
    private ConsoleManager() {
        MonitorManager.getInstance().registerMonitorParticipant(new StorageUsageMonitor());
        MonitorManager.getInstance().registerMonitorParticipant(new MemoryUsageMonitor());
        
        ScheduleManager.getInstance().addJob(new ReleaseMemJob());
        
        TaskSeedManager.getInstance().addTaskGenerator(new BackupWorldTaskGenerator());
    }
    
    public static ConsoleManager getInstance() {
        if(instance == null) {
            instance = new ConsoleManager();
        }
        return instance;
    }
    
    public int getMemoryTrendSize() {
        return ConfigManager.getInstance().getConfigAsInt("memory.trend.size", 1000);
    }
    
    public int getRequestTrackSize() {
        return ConfigManager.getInstance().getConfigAsInt("request.track.size", 1000);
    }
    
    public List<MemoryInfo> getMemoryTrend() {
        return this.infos;
    }
    
    public List<RequestInfo> getRequestInfos() {
        return this.requests;
    }
    
    public void trackMemoryUsage(double usage) {
        MemoryInfo info = new MemoryInfo();
        info.time = System.currentTimeMillis();
        info.percentage = (int) usage;
        
        this.infos.add(info);
        while(this.infos.size() > this.getMemoryTrendSize()) {
            this.infos.removeFirst();
        }
    }
    
    public void trackRequest(RequestInfo request) {
        if(request != null) {
            this.requests.add(request);
            while(this.requests.size() > this.getRequestTrackSize()) {
                this.requests.removeFirst();
            }
        }
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
        if(logger.isTraceEnabled()) {
            logger.trace("last line is " + last_line);
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
     * Get % (used, free)
     * 
     * @return
     */
    public double [] getMemoryUsageDisplay() {
        double [] ret = new double[2];
        long used = this.usedMemory();
        long max = this.maxMemory();
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
            if(logger.isTraceEnabled()) {
                logger.trace("get quota raw result : " + result);
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
            this.run("truncate -s 0 ../app-root/logs/jbossews.log");
            this.run("truncate -s 0 ../app-root/logs/mysql.log");
            this.run("truncate -s 0 ../app-root/logs/phpmyadmin.log");
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
    
    public void notifyStarted() {
        this.startedTime = System.currentTimeMillis();
    }
    
    public long getUpTime() {
        return System.currentTimeMillis() - this.startedTime;
    }
    
    public long maxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    public long totalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
    
    public long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
    
    public long usedMemory() {
        return this.totalMemory() - this.freeMemory();
    }
    
    public String getMemoryUsageDisplay(MemoryUsage usage) {
        if(usage == null) {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("<table class=\"table table-striped table-bordered\">");
        sb.append("<thead><tr><th>Type</th><th>Size</th></tr></thead>");
        
        sb.append("<tr><td>Init</td><td>");
        sb.append(SizeUtils.getSizeReadableString(usage.getInit()));
        sb.append("</td></tr>");
        
        sb.append("<tr><td>Max</td><td>");
        sb.append(SizeUtils.getSizeReadableString(usage.getMax()));
        sb.append("</td></tr>");
        
        sb.append("<tr><td>Used</td><td>");
        sb.append(SizeUtils.getSizeReadableString(usage.getUsed()));
        sb.append("</td></tr>");
        
        sb.append("<tr><td>Committed</td><td>");
        sb.append(SizeUtils.getSizeReadableString(usage.getCommitted()));
        sb.append("</td></tr>");
        
        sb.append("</table>");
        
        return sb.toString();
    }
    
    public void dumpHeap(String fileName) {
        HeapDumper.dumpHeap(fileName, true);
    }
    
    public List<FileInfo> listFiles(String path) {
        if(StringUtils.isBlank(path)) {
            path = "";
        }
        
        List<FileInfo> infos = new ArrayList<FileInfo>();
        File file = new File(ConfigManager.getInstance().getDataDir() + path);
        if(file.exists()) {
            if(file.isDirectory()) {
                for(File f : file.listFiles()) {
                    FileInfo info = this.toFileInfo(f, path);
                    if(info != null) {
                        infos.add(info);
                    }
                }
            }
        }
        
        Collections.sort(infos, new Comparator<FileInfo>(){

            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if(o1.name.endsWith("/") && !o2.name.endsWith("/")) {
                    return -1;
                }
                else if(!o1.name.endsWith("/") && o2.name.endsWith("/")) {
                    return 1;
                }
                else {
                    return o1.name.compareTo(o2.name);
                }
            }
            
        });
        
        return infos;
    }
    
    private FileInfo toFileInfo(File file, String base) {
        if(file == null) {
            return null;
        }
        if(StringUtils.isBlank(base)) {
            base = "";
        }
        else {
            if(!base.endsWith("/")) {
                base = base + "/";
            }
        }
        
        FileInfo info = new FileInfo();
        info.name = file.getName();
        if(file.isDirectory()) {
            if(!info.name.endsWith("/")) {
                info.name = info.name + "/";
            }
        }
        info.path = base + info.name;
        info.size = SizeUtils.getSizeReadableString(SizeUtils.getSize(file));
        return info;
    }
    
    public List<ObjectGraphInfo> getObjectGraphInfos() {
        List<ObjectGraphInfo> infos = new ArrayList<ObjectGraphInfo>();
        
        List<Object> managers = ManagerLoader.getManagers();
        for(Object manager : managers) {
            ObjectGraphInfo info = new ObjectGraphInfo();
            info.name = manager.getClass().getSimpleName();
            
            Footprint fp = ObjectGraphMeasurer.measure(manager);
            info.objects = fp.getObjects();
            info.refs = fp.getReferences();
            info.primitives = fp.getPrimitives().size();
            infos.add(info);
        }
        
        return infos;
    }
    
    public long [] getResponseTimeStats() {
        long min = -1;
        long max = -1;
        long sum = 0;
        for(RequestInfo info : this.requests) {
            if(min < 0 || min > info.duration) {
                min = info.duration;
            }
            if(max < 0 || max < info.duration) {
                max = info.duration;
            }
            sum += info.duration;
        }
        
        long [] ret = new long[3];
        if(this.requests.isEmpty()) {
            return ret;
        }
        
        ret[0] = sum / this.requests.size();
        ret[1] = min;
        ret[2] = max;
        
        return ret;
    }
    
    public Map<String, Double> getResponseTimeStatsInSections() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        if(this.requests.isEmpty()) {
            return ret;
        }
        
        long [] stats = this.getResponseTimeStats();
        int sections = 5;
        long min = stats[1];
        long max = stats[2];
        long step = (max - min) / sections;
        ResponseTimeSection [] rtsArray = new ResponseTimeSection[sections];
        for(int i = 0; i < rtsArray.length; i++) {
            ResponseTimeSection rts = new ResponseTimeSection();
            rts.start = min + i * step;
            rts.end = min + (i + 1) * step;
            rts.name = TimeUtils.getTimeReadableString(rts.start) + " to " + TimeUtils.getTimeReadableString(rts.end);
            rtsArray[i] = rts;
        }
        
        rtsArray[sections - 1].end = max;
        
        for(RequestInfo info : this.requests) {
            for(ResponseTimeSection rts : rtsArray) {
                if(rts.contains(info.duration)) {
                    rts.count += 1;
                    break;
                }
            }
        }
        
        for(ResponseTimeSection rts : rtsArray) {
            double pct = FormatUtils.getRoundedValue(rts.count * 100.0 / this.requests.size());
            ret.put(rts.name, pct);
        }
        
        return ret;
    }
    
    public Map<String, Double> getPageVisitStats() {
        Map<String, Double> ret = new HashMap<String, Double>();
        
        if(this.requests.isEmpty()) {
            return ret;
        }
        
        Map<String, Integer> data = new HashMap<String, Integer>();
        for(RequestInfo info : this.requests) {
            Integer i = data.get(info.requestURI);
            if(i == null) {
                i = 0;
            }
            i += 1;
            data.put(info.requestURI, i);
        }
        
        int total = this.requests.size();
        for(Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();
            double pct = FormatUtils.getRoundedValue(count * 100.0 / total);
            ret.put(key, pct);
        }
        
        return ret;
    }
}
