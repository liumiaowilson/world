package org.wilson.world.status;

import org.wilson.world.ext.Scriptable;

public interface StatusDeactivator {
    public static final String EXTENSION_POINT = "status.deactivate";
    
    @Scriptable(name = EXTENSION_POINT, description = "Deactivate the status. Assign the value of this action to status.", params = { })
    public void deactivate();
}
