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
import org.wilson.world.util.ValueHolder;

public class SystemFilter implements Filter {
    private static final Logger logger = Logger.getLogger(SystemFilter.class);
    
    @Override
    public void destroy() {
        logger.info("System filter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if(req instanceof HttpServletRequest) {
            ValueHolder.setRequest((HttpServletRequest)req);
        }
        
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("System filter initiated.");
    }

}
