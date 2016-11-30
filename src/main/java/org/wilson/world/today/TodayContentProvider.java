package org.wilson.world.today;

import javax.servlet.http.HttpServletRequest;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "today content providers", name = "today.content")
public interface TodayContentProvider {
    public String getName();
    
    /**
     * Return null to indicate that this content will not be shown
     * @return
     */
    public String getContent(HttpServletRequest request);
}
