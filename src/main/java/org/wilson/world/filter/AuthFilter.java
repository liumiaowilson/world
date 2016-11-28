package org.wilson.world.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.wilson.world.manager.SecManager;

public class AuthFilter implements Filter {
    private static final Logger logger = Logger.getLogger(AuthFilter.class);
    
    @Override
    public void destroy() {
        logger.info("Auth filter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        String token = (String) request.getSession().getAttribute("world-token");
        if(token == null || !SecManager.getInstance().isValidToken(token)) {
        	((HttpServletResponse)resp).sendRedirect("/signin.jsp");
        	return;
        }
        
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Auth filter initiated.");
    }

}
