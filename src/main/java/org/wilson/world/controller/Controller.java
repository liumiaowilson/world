package org.wilson.world.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Represent the controller for handling the logic and choosing the pages
 * 
 * @author mialiu
 *
 */
public interface Controller {
	
	/**
	 * Get the id of the controller
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * Set the id of the controller
	 * 
	 * @param id
	 */
	public void setId(int id);

	/**
	 * Get the name of the controller
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Check whether login is needed
	 * 
	 * @return
	 */
	public boolean isSecure();
	
	/**
	 * Get the uri of the controller
	 * 
	 * @return
	 */
	public String getUri();
	
	/**
	 * Execute the controller logic and return the page name
	 * 
	 * @param request
	 * @param response
	 * @param redirector
	 * @return
	 * @throws Exception
	 */
	public String execute(HttpServletRequest request, HttpServletResponse response, PageRedirector redirector) throws Exception;
}
