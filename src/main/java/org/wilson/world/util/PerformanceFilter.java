package org.wilson.world.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

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
            ValueHolder.setRequest((HttpServletRequest)req);
        }
        chain.doFilter(req, resp);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        if(logger.isDebugEnabled()) {
            logger.debug(name + " took " + elapsed + " ms.");
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Performance filter initiated.");
    }

}
