package org.wilson.world.today;

import javax.servlet.http.HttpServletRequest;

public interface TodayContentProvider {
    public String getName();
    
    /**
     * Return null to indicate that this content will not be shown
     * @return
     */
    public String getContent(HttpServletRequest request);
}
