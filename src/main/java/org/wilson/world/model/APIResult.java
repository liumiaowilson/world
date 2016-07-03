package org.wilson.world.model;

import java.util.List;

public class APIResult {
    
    public APIResultStatus status;
    
    public String message;
    
    public Object data;
    
    @SuppressWarnings("rawtypes")
    public List list;
}
