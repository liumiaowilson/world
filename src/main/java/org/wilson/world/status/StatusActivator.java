package org.wilson.world.status;

import org.wilson.world.ext.Scriptable;

public interface StatusActivator {
    public static final String EXTENSION_POINT = "status.activate";
    
    @Scriptable(name = EXTENSION_POINT, description = "Activate the status. Assign the value of this action to status.", params = { })
    public void activate();
}
