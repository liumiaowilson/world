package org.wilson.world.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.wilson.world.java.JavaExtensible;

/**
 * Base class for filter extension
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Extensible filter", name = "system.filter")
public abstract class ActiveFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}
	
	/**
	 * Get the name of the filter
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Get the pattern of the filter
	 * 
	 * @return
	 */
	public abstract String getPattern();

	/**
	 * Forced for an implementation
	 * 
	 */
	public abstract void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException;
}
