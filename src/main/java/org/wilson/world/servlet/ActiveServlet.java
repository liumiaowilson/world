package org.wilson.world.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Extensible servlet", name = "system.servlet")
public abstract class ActiveServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Get the name of the servlet
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Get the pattern of the servlet
	 * 
	 * @return
	 */
	public abstract String getPattern();

	/**
	 * Forced for an implementation
	 * 
	 */
	public abstract void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

	/**
	 * Forced for an implementation
	 * 
	 */
	public abstract void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

}
