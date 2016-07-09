package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;
import org.wilson.world.monitor.MonitorWorker;

public class MonitorManager implements ManagerLifecycle{
    private static final Logger logger = Logger.getLogger(MonitorManager.class);
    
    private static MonitorManager instance;
    
    private Map<String, Alert> alerts = new HashMap<String, Alert>();
    private List<MonitorParticipant> participants = new ArrayList<MonitorParticipant>();
    
    private MonitorWorker worker;
    private Thread workerThread;
    
    private MonitorManager() {
        
    }
    
    public static MonitorManager getInstance() {
        if(instance == null) {
            instance = new MonitorManager();
        }
        return instance;
    }
    
    public void start() {
        long interval = ConfigManager.getInstance().getConfigAsLong("monitor.interval", 60000);
        worker = new MonitorWorker(interval);
        workerThread = new Thread(worker);
        workerThread.start();
    }
    
    public void shutdown() {
        if(worker != null) {
            worker.setStopped(true);
        }
        try {
            if(workerThread != null) {
                workerThread.join();
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }
    
    public void registerMonitorParticipant(MonitorParticipant participant) {
        if(participant != null) {
            if(!this.participants.contains(participant)) {
                this.participants.add(participant);
            }
        }
    }
    
    public void unregisterMonitorParticipant(MonitorParticipant participant) {
        if(participant != null) {
            this.participants.remove(participant);
        }
    }
    
    public List<MonitorParticipant> getMonitorParticipants() {
        return this.participants;
    }
    
    public Map<String, Alert> getAlerts() {
        return this.alerts;
    }
    
    public void addAlert(String name, String content) {
        if(name == null || content == null) {
            return;
        }
        Alert alert = this.alerts.get(name);
        if(alert == null) {
            alert = new Alert();
            alert.id = name;
            alert.message = content;
            this.alerts.put(alert.id, alert);
        }
        else {
            alert.message = content;
        }
    }
    
    public void addAlert(Alert alert) {
        if(alert == null) {
            return;
        }
        this.alerts.put(alert.id, alert);
    }
    
    public void removeAlert(String name) {
        if(name == null) {
            return;
        }
        this.alerts.remove(name);
    }
    
    public void removeAlert(Alert alert) {
        if(alert == null) {
            return;
        }
        this.alerts.remove(alert.id);
    }
    
    public boolean hasAlert(String name) {
        return this.alerts.containsKey(name);
    }
}
