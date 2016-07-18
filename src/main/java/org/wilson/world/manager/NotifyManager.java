package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.notify.NotifyLevel;

public class NotifyManager {
    private static NotifyManager instance;
    
    private Map<String, List<String>> msgMap = new HashMap<String, List<String>>();
    
    private NotifyManager() {
        
    }
    
    public static NotifyManager getInstance() {
        if(instance == null) {
            instance = new NotifyManager();
        }
        return instance;
    }
    
    private synchronized void notify(String key, String message) {
        if(StringUtils.isBlank(key) || StringUtils.isBlank(message)) {
            return;
        }
        
        List<String> queue = this.msgMap.get(key);
        if(queue == null) {
            queue = new ArrayList<String>();
            this.msgMap.put(key, queue);
        }
        queue.add(message);
    }
    
    public synchronized List<String> take(String key) {
        if(StringUtils.isBlank(key)) {
            return new ArrayList<String>();
        }
        List<String> queue = this.msgMap.remove(key);
        return queue;
    }
    
    public void notify(NotifyLevel level, String message) {
        if(NotifyLevel.SUCCESS == level) {
            notify("notify_success", message);
        }
        else if(NotifyLevel.INFO == level) {
            notify("notify_info", message);
        }
        else if(NotifyLevel.WARNING == level) {
            notify("notify_warning", message);
        }
        else if(NotifyLevel.DANGER == level) {
            notify("notify_danger", message);
        }
    }
    
    public void notifySuccess(String message) {
        this.notify(NotifyLevel.SUCCESS, message);
    }
    
    public void notifyInfo(String message) {
        this.notify(NotifyLevel.INFO, message);
    }
    
    public void notifyWarning(String message) {
        this.notify(NotifyLevel.WARNING, message);
    }
    
    public void notifyDanger(String message) {
        this.notify(NotifyLevel.DANGER, message);
    }
}