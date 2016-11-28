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
import org.wilson.world.balance.BalanceStatus;
import org.wilson.world.manager.BalanceManager;

public class BalanceFilter implements Filter {
    private static final Logger logger = Logger.getLogger(BalanceFilter.class);
    
    @Override
    public void destroy() {
        logger.info("Balance filter destroyed.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if(BalanceManager.getInstance().isUnderProbation((HttpServletRequest) req)) {
            BalanceStatus status = BalanceManager.getInstance().check();
            if(!BalanceManager.getInstance().canProceed((HttpServletRequest) req, status)) {
                ((HttpServletResponse)resp).sendRedirect("/jsp/balance_broken.jsp");
                return;
            }
        }
        
        chain.doFilter(req, resp);

        BalanceManager.getInstance().probate((HttpServletRequest) req);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("Balance filter initiated.");
    }

}
