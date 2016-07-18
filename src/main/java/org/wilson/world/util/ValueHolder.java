package org.wilson.world.util;

import javax.servlet.http.HttpServletRequest;

public class ValueHolder {
    private static ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
    
    public static HttpServletRequest getRequest() {
        return request.get();
    }
    
    public static void setRequest(HttpServletRequest req) {
        request.set(req);
    }
}
