package org.wilson.world.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class DemoFilter extends ActiveFilter {
	private static final Logger logger = Logger.getLogger(DemoFilter.class);
	
	@Override
	public String getName() {
		return "demo";
	}

	@Override
	public String getPattern() {
		return "/servlet/ext/demo";
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		logger.info("Demo filter invoked.");
		
		chain.doFilter(req, resp);
	}

}
