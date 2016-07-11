package org.wilson.world.ext;

import java.util.HashMap;
import java.util.Map;

public class ExtensionPoint {
    public String name;
    
    @SuppressWarnings("rawtypes")
    public Map<String, Class> params = new HashMap<String, Class>();
}
