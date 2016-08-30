package org.wilson.world.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONUtils {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object convert(Object jsonObj) {
        if(jsonObj == null) {
            return null;
        }
        
        if(jsonObj instanceof JSONArray) {
            JSONArray array = (JSONArray)jsonObj;
            List list = new ArrayList();
            for(int i = 0; i < array.size(); i++) {
                Object obj = array.get(i);
                obj = convert(obj);
                if(obj != null) {
                    list.add(obj);
                }
            }
            return list;
        }
        else if(jsonObj instanceof JSONObject) {
            JSONObject obj = (JSONObject)jsonObj;
            Map map = new HashMap();
            for(Object key : obj.keySet()) {
                String keyStr = (String)key;
                Object o = obj.get(keyStr);
                o = convert(o);
                if(o != null) {
                    map.put(keyStr, o);
                }
            }
            return map;
        }
        else {
            return jsonObj;
        }
    }
}
