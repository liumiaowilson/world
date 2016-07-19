package org.wilson.world.manager;

public class URLManager {
    private String lastUrl;
    private String currentUrl;
    
    private static URLManager instance;
    
    private URLManager() {
        
    }
    
    public static URLManager getInstance() {
        if(instance == null) {
            instance = new URLManager();
        }
        return instance;
    }
    
    public void setCurrentUrl(String currentUrl) {
        this.lastUrl = this.currentUrl;
        this.currentUrl = currentUrl;
    }
    
    public String getCurrentUrl() {
        return this.currentUrl;
    }
    
    public String getLastUrl() {
        return this.lastUrl;
    }
}
