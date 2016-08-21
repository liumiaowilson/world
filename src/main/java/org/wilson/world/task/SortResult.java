package org.wilson.world.task;

import java.util.HashMap;
import java.util.Map;

public class SortResult {
    public String reason;
    
    public int result;
    
    private static Map<String, SortResult> results = new HashMap<String, SortResult>();
    
    public static SortResult create(String reason, int result) {
        SortResult ret = results.get(reason);
        if(ret == null) {
            ret = new SortResult();
            ret.reason = reason;
            results.put(reason, ret);
        }
        ret.result = result;
        
        return ret;
    }
}
