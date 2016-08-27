package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.menu.MenuItem;

public class DefaultBalanceMatcher implements BalanceMatcher {
    private MenuItem item;
    private String prefix = "javascript:jumpTo('";
    private String suffix = "')";
    private String uri;
    
    public DefaultBalanceMatcher() {
    }
    
    public DefaultBalanceMatcher(MenuItem item) {
        this.setMenuItem(item);
    }
    
    public MenuItem getMenuItem() {
        return this.item;
    }
    
    public void setMenuItem(MenuItem item) {
        this.item = item;
    }
    
    protected boolean match(HttpServletRequest request, String uri) {
        if(StringUtils.isBlank(uri)) {
            return false;
        }
        
        String str = request.getRequestURI() + "?" + request.getQueryString();
        return str.startsWith(uri);
    }
    
    protected String getMenuURI(MenuItem item) {
        if(item == null) {
            return null;
        }
        
        String link = this.item.link;
        if(StringUtils.isBlank(link)) {
            return null;
        }
        
        if(link.startsWith(prefix) && link.endsWith(suffix)) {
            int pos = link.lastIndexOf(suffix);
            String page = link.substring(prefix.length(), pos);
            String uri = "/jsp/" + page;
            return uri;
        }
        
        return null;
    }
    
    @Override
    public boolean match(HttpServletRequest request) {
        if(request == null) {
            return false;
        }
        
        if(this.uri == null) {
            this.uri = this.getMenuURI(this.item);
        }
        
        return this.match(request, this.uri);
    }

}
