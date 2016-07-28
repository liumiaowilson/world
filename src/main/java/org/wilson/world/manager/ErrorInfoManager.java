package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.model.ErrorInfo;
import org.wilson.world.usage.NumOfErrorsMonitor;

public class ErrorInfoManager {
    private static ErrorInfoManager instance;
    
    private Map<Integer, ErrorInfo> infos = new HashMap<Integer, ErrorInfo>();
    
    public static int GLOBAL_ID = 1;
    
    private NumOfErrorsMonitor monitor;
    
    private ErrorInfoManager() {
        this.monitor = new NumOfErrorsMonitor();
        MonitorManager.getInstance().registerMonitorParticipant(monitor);
    }
    
    public static ErrorInfoManager getInstance() {
        if(instance == null) {
            instance = new ErrorInfoManager();
        }
        return instance;
    }
    
    public NumOfErrorsMonitor getMonitor() {
        return this.monitor;
    }
    
    public void createErrorInfo(ErrorInfo info) {
        if(info != null) {
            info.id = GLOBAL_ID++;
            this.infos.put(info.id, info);
        }
    }
    
    public ErrorInfo getErrorInfo(int id) {
        return this.infos.get(id);
    }
    
    public List<ErrorInfo> getErrorInfos() {
        return new ArrayList<ErrorInfo>(this.infos.values());
    }
    
    public void updateErrorInfo(ErrorInfo info) {
        this.infos.put(info.id, info);
    }
    
    public void deleteErrorInfo(int id) {
        this.infos.remove(id);
    }
    
    public void clear() {
        this.infos.clear();
    }
}
