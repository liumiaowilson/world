package org.wilson.world.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.controller.Controller;
import org.wilson.world.controller.PageRedirector;
import org.wilson.world.manager.ControllerManager;
import org.wilson.world.manager.NotifyManager;

public class ControllerServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(ControllerServlet.class);
    
    public static final String SECURE_PREFIX = "/jsp_ext/";
    public static final String INSECURE_PREFIX = "/public_ext/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	this.doService(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	this.doService(request, response);
    }
    
    private void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String uri = request.getRequestURI();
    	if(StringUtils.isBlank(uri)) {
    		jumpToHomePage(response, "Invalid request URI.");
    		return;
    	}
    	
    	Controller controller = ControllerManager.getInstance().getControllerByUri(uri);
    	if(controller == null) {
    		jumpToHomePage(response, "No controller could be found.");
    		return;
    	}
    	
    	String pageName = null;
    	PageRedirector redirector = new PageRedirector();
    	try {
    		pageName = controller.execute(request, response, redirector);
    	}
    	catch(Exception e) {
    		logger.error(e);
    		jumpToHomePage(response, e.getMessage());
    		return;
    	}
    	
    	if(StringUtils.isNotBlank(pageName)) {
    		redirector.redirect(response, pageName);
    	}
    	else {
    		jumpToHomePage(response, null);
    	}
    }
    
    public static void jumpToHomePage(HttpServletResponse response, String message) throws IOException {
    	if(!StringUtils.isBlank(message)) {
    		NotifyManager.getInstance().notifyDanger(message);
    	}
    	response.sendRedirect("/index.jsp");
    }
}
