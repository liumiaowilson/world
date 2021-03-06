package org.wilson.world.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.wilson.world.console.RequestInfo;
import org.wilson.world.manager.ConsoleManager;

public class PerformanceFilter implements Filter {
    private static final Logger logger = Logger.getLogger(PerformanceFilter.class);
    
    @Override
    public void destroy() {
        logger.info("Performance filter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();
        String name = "servlet";
        if(req instanceof HttpServletRequest) {
            name = ((HttpServletRequest)req).getRequestURI();
        }
        
        String request_end_time = req.getParameter("_request_end_time");
        String request_start_time = req.getParameter("_request_start_time");
        
        chain.doFilter(req, resp);

        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if(logger.isTraceEnabled()) {
            logger.trace(name + " took " + elapsed + " ms.");
        }
        
        RequestInfo info = new RequestInfo();
        info.requestURI = name;
        info.time = start;
        info.duration = elapsed;
        ConsoleManager.getInstance().trackRequest(info, request_end_time, request_start_time);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Performance filter initiated.");
    }

}
