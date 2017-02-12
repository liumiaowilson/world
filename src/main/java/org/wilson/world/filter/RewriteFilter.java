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
import org.wilson.world.manager.RewriteManager;

public class RewriteFilter implements Filter {
    private static final Logger logger = Logger.getLogger(RewriteFilter.class);
    
    @Override
    public void destroy() {
        logger.info("Rewrite filter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();
        String rewritePath = RewriteManager.getInstance().rewrite(path);
        if(rewritePath != null) {
        	request.getRequestDispatcher(rewritePath).forward(req, resp);
        }
        else {
        	chain.doFilter(req, resp);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Rewrite filter initiated.");
    }

}
