package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class URLManager {
    private List<String> urls = new ArrayList<String>();
    private String baseUrl;
    
    private int limit = 10;
    
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
        if(StringUtils.isBlank(currentUrl)) {
            return;
        }
        if(!this.urls.isEmpty() && this.urls.get(this.urls.size() - 1).equals(currentUrl)) {
            return;
        }
        
        this.urls.add(currentUrl);
        if(this.urls.size() > limit) {
            this.urls.remove(0);
        }
    }
    
    public String getCurrentUrl() {
        if(this.urls.isEmpty()) {
            return this.baseUrl + "/index.jsp";
        }
        return this.urls.get(this.urls.size() - 1);
    }
    
    public String getLastUrl() {
        if(this.urls.size() < 2) {
            return this.baseUrl + "/index.jsp";
        }
        String lastUrl = null;
        for(int i = this.urls.size() - 2; i >= 0; i--) {
            String url = this.urls.get(i);
            if(!url.contains("_new.jsp") && !url.contains("_edit.jsp")) {
                lastUrl = url;
                break;
            }
        }
        if(lastUrl == null) {
            return this.baseUrl + "/index.jsp";
        }
        else {
            return lastUrl;
        }
    }
}
