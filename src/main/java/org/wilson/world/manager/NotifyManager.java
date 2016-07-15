package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.notify.NotifyLevel;

public class NotifyManager {
    private static NotifyManager instance;
    
    private ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();
    
    private NotifyManager() {
        
    }
    
    public static NotifyManager getInstance() {
        if(instance == null) {
            instance = new NotifyManager();
        }
        return instance;
    }
    
    public void setRequest(HttpServletRequest request) {
        this.requestHolder.set(request);
    }
    
    public HttpServletRequest getRequest() {
        return this.requestHolder.get();
    }
    
    @SuppressWarnings("unchecked")
    private synchronized void notify(String key, String message) {
        if(this.getRequest() == null) {
            return;
        }
        List<String> msgs = (List<String>) this.getRequest().getSession().getAttribute(key);
        if(msgs == null) {
            msgs = new ArrayList<String>();
        }
        msgs.add(message);
        this.getRequest().getSession().setAttribute(key, msgs);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized List<String> take(String key) {
        if(this.getRequest() == null) {
            return new ArrayList<String>();
        }
        List<String> msgs = (List<String>) this.getRequest().getSession().getAttribute(key);
        this.getRequest().getSession().setAttribute(key, null);
        return msgs;
    }
    
    public void notify(NotifyLevel level, String message) {
        if(this.getRequest() == null) {
            return;
        }
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
