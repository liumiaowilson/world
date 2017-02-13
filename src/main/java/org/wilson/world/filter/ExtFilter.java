package org.wilson.world.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.wilson.world.manager.FilterManager;

public class ExtFilter implements Filter {
	private static final Logger logger = Logger.getLogger(ExtFilter.class);
	
	@Override
	public void destroy() {
		logger.info("ExtFilter destroyed.");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		String url = ((HttpServletRequest) req).getRequestURI();
		List<ActiveFilter> filters = FilterManager.getInstance().getMatchingActiveFilters(url);
		ExtFilterChain extChain = new ExtFilterChain(filters, chain);
		extChain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("ExtFilter initiated.");
	}

}
