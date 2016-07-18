package org.wilson.world.context;

import org.wilson.world.ext.Scriptable;

public interface ContextInitializer {
    public static final String EXTENSION_POINT = "context.init";
    
    @Scriptable(name = EXTENSION_POINT, description = "Set the current context after user has logged in.", params = {})
    public void setCurrentContext();
}
