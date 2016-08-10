package org.wilson.world.web;

import org.wilson.world.ext.Scriptable;

public interface WebJobExecutor {
    public static final String EXTENSION_POINT = "web.job.action";
    
    @Scriptable(name = EXTENSION_POINT, description = "Execute the web job action. Assign the value of this action to hopper.", params = { "monitor" })
    public void execute(WebJobMonitor monitor) throws Exception;
}
