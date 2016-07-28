package org.wilson.world.manager;

public class URLManager {
    private String lastUrl;
    private String currentUrl;
    private String baseUrl;
    
    private static URLManager instance;
    
    private URLManager() {
        
    }
    
    public static URLManager getInstance() {
        if(instance == null) {
            instance = new URLManager();
        }
        return instance;
    }
    
    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
    
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    public void setCurrentUrl(String currentUrl) {
        if(this.currentUrl != null && this.currentUrl.equals(currentUrl)) {
            return;
        }
        this.lastUrl = this.currentUrl;
        this.currentUrl = currentUrl;
    }
    
    public String getCurrentUrl() {
        if(this.currentUrl == null) {
            return this.baseUrl + "/index.jsp";
        }
        return this.currentUrl;
    }
    
    public String getLastUrl() {
        if(this.lastUrl == null) {
            return this.baseUrl + "/index.jsp";
        }
        return this.lastUrl;
    }
}
