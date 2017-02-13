package org.wilson.world.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ExtFilterChain implements FilterChain {
	private List<ActiveFilter> filters;
	private FilterChain chain;
	
	public ExtFilterChain(List<ActiveFilter> filters, FilterChain chain) {
		this.filters = filters;
		this.chain = chain;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
		if(!this.filters.isEmpty()) {
			ActiveFilter filter = this.filters.remove(0);
			filter.doFilter(req, resp, this);
		}
		else {
			chain.doFilter(req, resp);
		}
	}

}
