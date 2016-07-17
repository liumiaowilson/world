package org.wilson.world.task;

import org.wilson.world.ext.Scriptable;

public interface TaskDefaultValueProvider {
    public static final String EXTENSION_POINT = "task.default.value";
    
    @Scriptable(name = EXTENSION_POINT, description = "Provide default values for task attributes.", params = { "name" })
    public String getDefaultValue(String name);
}
