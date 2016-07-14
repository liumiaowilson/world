package org.wilson.world.manager;

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
    
    public void notify(NotifyLevel level, String message) {
        if(this.getRequest() == null) {
            return;
        }
        if(NotifyLevel.SUCCESS == level) {
            this.getRequest().getSession().setAttribute("notify_success", message);
        }
        else if(NotifyLevel.INFO == level) {
            this.getRequest().getSession().setAttribute("notify_info", message);
        }
        else if(NotifyLevel.WARNING == level) {
            this.getRequest().getSession().setAttribute("notify_warning", message);
        }
        else if(NotifyLevel.DANGER == level) {
            this.getRequest().getSession().setAttribute("notify_danger", message);
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
