package org.wilson.world.balance;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.menu.MenuItem;

public class DefaultBalanceMatcher implements BalanceMatcher {
    private MenuItem item;
    private String prefix = "javascript:jumpTo('";
    private String suffix = "')";
    
    public DefaultBalanceMatcher(MenuItem item) {
        this.item = item;
    }
    
    protected MenuItem getMenuItem() {
        return this.item;
    }
    
    @Override
    public boolean match(HttpServletRequest request) {
        String link = this.item.link;
        if(StringUtils.isBlank(link)) {
            return false;
        }
        
        if(link.startsWith(prefix) && link.endsWith(suffix)) {
            int pos = link.lastIndexOf(suffix);
            String page = link.substring(prefix.length(), pos);
            String uri = "/jsp/" + page;
            return uri.equals(request.getRequestURI());
        }
        
        return false;
    }

}
